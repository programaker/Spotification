package spotification.playlist

import cats.data.NonEmptyList
import cats.implicits._
import eu.timepit.refined.auto._
import spotification.authorization.program.{SpotifyAuthorizationEnv, requestAccessTokenProgram}
import spotification.authorization.{AccessToken, RefreshToken}
import spotification.common.{CurrentUri, NextUri, UriString}
import spotification.config.RetryConfig
import spotification.config.service.{PlaylistConfigEnv, playlistConfig}
import spotification.effect.refineRIO
import spotification.log.service.{LogEnv, info}
import spotification.playlist.GetPlaylistsItemsRequest.RequestType.First
import spotification.playlist.GetPlaylistsItemsResponse.TrackResponse
import spotification.playlist.service.{
  PlaylistServiceEnv,
  addItemsToPlaylist,
  getPlaylistItems,
  removeItemsFromPlaylist
}
import spotification.track.TrackUri
import zio.clock.Clock
import zio.duration.Duration
import zio.{RIO, Schedule, ZIO}

package object program {
  type ReleaseRadarNoSinglesEnv = LogEnv with PlaylistServiceEnv with PlaylistConfigEnv with SpotifyAuthorizationEnv
  type MergePlaylistsEnv = Clock with LogEnv with PlaylistServiceEnv with PlaylistConfigEnv with SpotifyAuthorizationEnv

  def releaseRadarNoSinglesProgram(
    refreshToken: RefreshToken,
    releaseRadarId: PlaylistId,
    releaseRadarNoSinglesId: PlaylistId
  ): RIO[ReleaseRadarNoSinglesEnv, Unit] =
    for {
      accessToken    <- requestAccessTokenProgram(refreshToken)
      playlistConfig <- playlistConfig

      limit = playlistConfig.getPlaylistItemsLimit
      firstRequest = GetPlaylistsItemsRequest.first(accessToken, _, limit)

      _ <- info(show"Cleaning up release-radar-no-singles($releaseRadarNoSinglesId)")
      _ <- clearPlaylist(firstRequest(releaseRadarNoSinglesId))

      _ <- info(show"Feeding release-radar-no-singles using release-radar($releaseRadarId)")
      _ <- paginatePlaylistPar(firstRequest(releaseRadarId)) { tracks =>
        val trackUris = tracks.toList.mapFilter(trackUriIfAlbum)
        NonEmptyList.fromList(trackUris).map(importTracks(_, releaseRadarNoSinglesId, accessToken)).getOrElse(RIO.unit)
      }

      _ <- info("Done!")
    } yield ()

  def mergePlaylistsProgram(
    refreshToken: RefreshToken,
    mergedPlaylistId: PlaylistId,
    playlistsToMerge: List[PlaylistId]
  ): RIO[MergePlaylistsEnv, Unit] =
    for {
      accessToken    <- requestAccessTokenProgram(refreshToken)
      playlistConfig <- playlistConfig

      limit = playlistConfig.getPlaylistItemsLimit
      firstRequest = GetPlaylistsItemsRequest.first(accessToken, _, limit)
      retry = playlistConfig.mergePlaylistsRetry

      _ <- info(show"Cleaning up merged-playlist($mergedPlaylistId)")
      _ <- clearPlaylist(firstRequest(mergedPlaylistId))

      _ <- info(show"Feeding merged-playlist using $playlistsToMerge")
      _ <- mergePlaylists(playlistsToMerge, mergedPlaylistId, firstRequest, retry)

      _ <- info("Done!")
    } yield ()

  private def paginatePlaylistPar[R <: PlaylistServiceEnv](req: GetPlaylistsItemsRequest[First])(
    f: NonEmptyList[TrackResponse] => RIO[R, Unit]
  ): RIO[R, Unit] =
    paginatePlaylist(req)(f)((_, nextUri) => nextUri)(_ &> _)

  /**
   * Uses GetPlaylistsItemsRequest to fetch items from a playlist with pagination.
   * This function has a strange signature, but the trade-off is "unparalleled type-inference"
   *
   * @param req The initial request
   * @param processTracks A function to process the tracks in a page
   *
   * @param chooseUri A function to choose whether to go to the next page
   * or stay in the current (useful to delete tracks for instance)
   *
   * @param combinePageEffects A function to combine the result of processing
   * the current page with the result of the next page request (useful to choose if parallel or not)
   */
  private def paginatePlaylist[R <: PlaylistServiceEnv](
    req: GetPlaylistsItemsRequest[First]
  )(
    processTracks: NonEmptyList[TrackResponse] => RIO[R, Unit]
  )(
    chooseUri: (CurrentUri, NextUri) => UriString
  )(
    combinePageEffects: (RIO[R, Unit], RIO[R, Unit]) => RIO[R, Unit]
  ): RIO[R, Unit] = {
    def loop(req: GetPlaylistsItemsRequest[_]): RIO[R, Unit] =
      getPlaylistItems(req).flatMap { resp =>
        NonEmptyList
          .fromList(resp.items.flatMap(_.track))
          .map { tracks =>
            val thisPage = processTracks(tracks)

            val nextPage = resp.next match {
              case None =>
                RIO.unit
              case Some(nextUri) =>
                val uri = chooseUri(resp.href, nextUri)
                loop(GetPlaylistsItemsRequest.next(req.accessToken, uri))
            }

            combinePageEffects(thisPage, nextPage)
          }
          .getOrElse(RIO.unit)
      }

    loop(req)
  }

  private def clearPlaylist(req: GetPlaylistsItemsRequest[First]): RIO[PlaylistServiceEnv, Unit] =
    // `(currentUri, _) => currentUri`:
    // since we are deleting tracks,
    // we should always stay in the first page
    paginatePlaylist(req)(deleteTracks(_, req))((currentUri, _) => currentUri)(_ *> _)

  private def importTracks(
    trackUris: NonEmptyList[TrackUri],
    destPlaylist: PlaylistId,
    accessToken: AccessToken
  ): RIO[PlaylistServiceEnv, Unit] = {
    val iterable =
      trackUris.toList
        .to(LazyList)
        .grouped(PlaylistItemsToProcess.MaxSize)
        .map(_.toVector)
        .map(refineRIO[PlaylistServiceEnv, PlaylistItemsToProcessR](_))
        .map(_.flatMap(importTrackChunk(_, destPlaylist, accessToken)))
        .to(Iterable)

    ZIO.foreachPar_(iterable)(identity)
  }

  private def deleteTracks(
    items: NonEmptyList[TrackResponse],
    req: GetPlaylistsItemsRequest[First]
  ): RIO[PlaylistServiceEnv, Unit] = {
    val iterable =
      items.toList
        .to(LazyList)
        .map(_.uri)
        .grouped(PlaylistItemsToProcess.MaxSize)
        .map(_.toVector)
        .map(refineRIO[PlaylistServiceEnv, PlaylistItemsToProcessR](_))
        .map(_.map(RemoveItemsFromPlaylistRequest.make(_, req.requestType.playlistId, req.accessToken)))
        .map(_.flatMap(removeItemsFromPlaylist))
        .to(Iterable)

    ZIO.foreachPar_(iterable)(identity)
  }

  private def importTrackChunk(
    trackUris: PlaylistItemsToProcess[TrackUri],
    destPlaylist: PlaylistId,
    accessToken: AccessToken
  ): RIO[PlaylistServiceEnv, Unit] =
    addItemsToPlaylist(AddItemsToPlaylistRequest.make(accessToken, destPlaylist, trackUris)).map(_ => ())

  private def mergePlaylists(
    sources: List[PlaylistId],
    dest: PlaylistId,
    mkReq: PlaylistId => GetPlaylistsItemsRequest[First],
    retry: RetryConfig
  ): RIO[MergePlaylistsEnv, Unit] =
    NonEmptyList
      .fromList(sources)
      .map { playlists =>
        ZIO.foreachPar_(playlists.toIterable) { playlist =>
          info(show"> playlist($playlist) is being imported") *>
            importPlaylist(mkReq(playlist), dest, retry) *>
            info(show"< playlist($playlist) done")
        }
      }
      .getOrElse(RIO.unit)

  private def importPlaylist(
    source: GetPlaylistsItemsRequest[First],
    dest: PlaylistId,
    retry: RetryConfig
  ): RIO[MergePlaylistsEnv, Unit] =
    paginatePlaylistPar(source) { tracks =>
      importTracks(tracks.map(_.uri), dest, source.accessToken)
    }.retry(Schedule.exponential(Duration.fromScala(retry.retryAfter)) && Schedule.recurs(retry.attempts))
}
