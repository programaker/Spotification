package spotification.playlist

import cats.data.NonEmptyList
import cats.implicits._
import eu.timepit.refined.auto._
import spotification.authorization.application.{SpotifyAuthorizationEnv, requestAccessTokenProgram}
import spotification.common.application.refineRIO
import spotification.common.{CurrentUri, NextUri, UriString}
import spotification.config.RetryConfig
import spotification.config.application.{PlaylistConfigEnv, playlistConfig}
import spotification.log.application.{LogEnv, info}
import spotification.authorization.{AccessToken, RefreshToken}
import spotification.playlist.GetPlaylistsItemsRequest.{FirstRequest, NextRequest}
import spotification.playlist.GetPlaylistsItemsResponse.TrackResponse
import spotification.track.TrackUri
import zio._
import zio.clock.Clock
import zio.duration.Duration

package object application {
  type PlaylistServiceEnv = Has[PlaylistService]

  type ReleaseRadarNoSinglesEnv = LogEnv with PlaylistServiceEnv with PlaylistConfigEnv with SpotifyAuthorizationEnv

  type MergePlaylistsEnv = Clock with LogEnv with PlaylistServiceEnv with PlaylistConfigEnv with SpotifyAuthorizationEnv

  def getPlaylistItems(req: GetPlaylistsItemsRequest): RIO[PlaylistServiceEnv, GetPlaylistsItemsResponse] =
    ZIO.accessM(_.get.getPlaylistsItems(req))

  def addItemsToPlaylist(req: AddItemsToPlaylistRequest): RIO[PlaylistServiceEnv, PlaylistSnapshotResponse] =
    ZIO.accessM(_.get.addItemsToPlaylist(req))

  def removeItemsFromPlaylist(req: RemoveItemsFromPlaylistRequest): RIO[PlaylistServiceEnv, PlaylistSnapshotResponse] =
    ZIO.accessM(_.get.removeItemsFromPlaylist(req))

  def releaseRadarNoSinglesProgram(
    refreshToken: RefreshToken,
    releaseRadarId: PlaylistId,
    releaseRadarNoSinglesId: PlaylistId
  ): RIO[ReleaseRadarNoSinglesEnv, Unit] =
    for {
      accessToken    <- requestAccessTokenProgram(refreshToken)
      playlistConfig <- playlistConfig

      limit = playlistConfig.getPlaylistItemsLimit
      firstRequest = FirstRequest.make(_, limit, accessToken)

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
      firstRequest = FirstRequest.make(_, limit, accessToken)
      retry = playlistConfig.mergePlaylistsRetry

      _ <- info(show"Cleaning up merged-playlist($mergedPlaylistId)")
      _ <- clearPlaylist(firstRequest(mergedPlaylistId))

      _ <- info(show"Feeding merged-playlist using $playlistsToMerge")
      _ <- mergePlaylists(playlistsToMerge, mergedPlaylistId, firstRequest, retry)

      _ <- info("Done!")
    } yield ()

  private def paginatePlaylistPar[R <: PlaylistServiceEnv](req: GetPlaylistsItemsRequest.FirstRequest)(
    f: NonEmptyList[TrackResponse] => RIO[R, Unit]
  ): RIO[R, Unit] =
    paginatePlaylist(req)(f)((_, nextUri) => nextUri)(_ &> _)

  /** Uses GetPlaylistsItemsRequest to fetch items from a playlist with pagination.
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
   * */
  private def paginatePlaylist[R <: PlaylistServiceEnv](
    req: GetPlaylistsItemsRequest
  )(
    processTracks: NonEmptyList[TrackResponse] => RIO[R, Unit]
  )(
    chooseUri: (CurrentUri, NextUri) => UriString
  )(
    combinePageEffects: (RIO[R, Unit], RIO[R, Unit]) => RIO[R, Unit]
  ): RIO[R, Unit] = {
    def loop(req: GetPlaylistsItemsRequest): RIO[R, Unit] =
      getPlaylistItems(req).flatMap { resp =>
        NonEmptyList
          .fromList(resp.items.flatMap(_.track))
          .map { tracks =>
            val thisPage = processTracks(tracks)

            val nextPage = resp.next match {
              case None =>
                RIO.unit
              case Some(nextUri) =>
                val accessToken = accessTokenFromRequest(req)
                val uri = chooseUri(resp.href, nextUri)
                loop(NextRequest(uri, accessToken))
            }

            combinePageEffects(thisPage, nextPage)
          }
          .getOrElse(RIO.unit)
      }

    loop(req)
  }

  private def clearPlaylist(req: GetPlaylistsItemsRequest.FirstRequest): RIO[PlaylistServiceEnv, Unit] =
    // `(currentUri, _) => currentUri`:
    // since we are deleting tracks,
    // we should always stay in the first page
    paginatePlaylist(req)(deleteTracks(_, req))((currentUri, _) => currentUri)(_ *> _)

  private def importTracks(
    trackUris: NonEmptyList[TrackUri],
    destPlaylist: PlaylistId,
    accessToken: AccessToken
  ): RIO[PlaylistServiceEnv, Unit] =
    ZIO.foreachPar_ {
      trackUris.toList
        .to(LazyList)
        .grouped(PlaylistItemsToProcess.MaxSize)
        .map(_.toVector)
        .map(refineRIO[PlaylistServiceEnv, PlaylistItemsToProcessR](_))
        .map(_.flatMap(importTrackChunk(_, destPlaylist, accessToken)))
        .to(Iterable)
    }(identity)

  private def deleteTracks(
    items: NonEmptyList[TrackResponse],
    req: GetPlaylistsItemsRequest.FirstRequest
  ): RIO[PlaylistServiceEnv, Unit] =
    ZIO.foreachPar_(
      items.toList
        .to(LazyList)
        .map(_.uri)
        .grouped(PlaylistItemsToProcess.MaxSize)
        .map(_.toVector)
        .map(refineRIO[PlaylistServiceEnv, PlaylistItemsToProcessR](_))
        .map(_.map(RemoveItemsFromPlaylistRequest.make(_, req.playlistId, req.accessToken)))
        .map(_.flatMap(removeItemsFromPlaylist))
        .to(Iterable)
    )(identity)

  private def importTrackChunk(
    trackUris: PlaylistItemsToProcess[TrackUri],
    destPlaylist: PlaylistId,
    accessToken: AccessToken
  ): RIO[PlaylistServiceEnv, Unit] =
    addItemsToPlaylist(AddItemsToPlaylistRequest.make(destPlaylist, trackUris, accessToken)).map(_ => ())

  private def mergePlaylists(
    sources: List[PlaylistId],
    dest: PlaylistId,
    mkReq: PlaylistId => FirstRequest,
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
    source: FirstRequest,
    dest: PlaylistId,
    retry: RetryConfig
  ): RIO[MergePlaylistsEnv, Unit] =
    paginatePlaylistPar(source) { tracks =>
      importTracks(tracks.map(_.uri), dest, source.accessToken)
    }.retry(Schedule.exponential(Duration.fromScala(retry.retryAfter)) && Schedule.recurs(retry.attempts))
}
