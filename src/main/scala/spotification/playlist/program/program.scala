package spotification.playlist

import cats.implicits._
import eu.timepit.refined.auto._
import spotification.album.service.{GetAlbumSampleTrackServiceR, getAlbumSampleTrack}
import spotification.album.{GetAlbumSampleTrackRequest, isAnniversaryAlbum}
import spotification.artist.program.{paginateArtistsAlbumsPar, paginateMyFollowedArtistsPar}
import spotification.artist.service.{GetArtistsAlbumsServiceR, GetMyFollowedArtistsServiceR}
import spotification.artist.{GetArtistsAlbumsRequest, GetArtistsAlbumsResponse, GetMyFollowedArtistsRequest}
import spotification.authorization.program.{RequestAccessTokenProgramR, requestAccessTokenProgram}
import spotification.authorization.{AccessToken, RefreshToken}
import spotification.common.MonthDay
import spotification.common.program.{PageRIO, paginate}
import spotification.config.RetryConfig
import spotification.config.service.{PlaylistConfigR, playlistConfig}
import spotification.effect.{refineRIO, unitRIO}
import spotification.log.service.{LogR, info}
import spotification.playlist.GetPlaylistsItemsRequest.RequestType.First
import spotification.playlist.GetPlaylistsItemsResponse.TrackResponse
import spotification.playlist.service._
import spotification.track.TrackUri
import spotification.user.GetMyProfileRequest
import spotification.user.service.{GetMyProfileServiceR, getMyProfile}
import zio.clock.Clock
import zio.duration.Duration
import zio.{RIO, Schedule, ZIO, clock}

package object program {
  type ReleaseRadarNoSinglesProgramR =
    RequestAccessTokenProgramR
      with PlaylistConfigR
      with LogR
      with GetPlaylistItemsServiceR
      with RemoveItemsFromPlaylistServiceR
      with AddItemsToPlaylistServiceR

  type MergePlaylistsProgramR =
    RequestAccessTokenProgramR
      with PlaylistConfigR
      with LogR
      with GetPlaylistItemsServiceR
      with RemoveItemsFromPlaylistServiceR
      with AddItemsToPlaylistServiceR
      with Clock

  type AlbumAnniversariesPlaylistProgramR = AddItemsToPlaylistServiceR
    with GetAlbumSampleTrackServiceR
    with GetArtistsAlbumsServiceR
    with GetMyFollowedArtistsServiceR
    with CreatePlaylistServiceR
    with Clock
    with GetMyProfileServiceR
    with RequestAccessTokenProgramR

  type PlaylistProgramsR = ReleaseRadarNoSinglesProgramR
    with MergePlaylistsProgramR
    with AlbumAnniversariesPlaylistProgramR

  def releaseRadarNoSinglesProgram(
    refreshToken: RefreshToken,
    releaseRadarId: PlaylistId,
    releaseRadarNoSinglesId: PlaylistId
  ): RIO[ReleaseRadarNoSinglesProgramR, Unit] =
    for {
      accessToken    <- requestAccessTokenProgram(refreshToken)
      playlistConfig <- playlistConfig

      limit = playlistConfig.getPlaylistItemsLimit
      firstRequest = GetPlaylistsItemsRequest.first(accessToken, _, limit)

      _ <- info(show"Cleaning up release-radar-no-singles($releaseRadarNoSinglesId)")
      _ <- clearPlaylist(firstRequest(releaseRadarNoSinglesId))

      _ <- info(show"Feeding release-radar-no-singles using release-radar($releaseRadarId)")
      _ <- paginatePlaylistPar(firstRequest(releaseRadarId)) { tracks =>
        importTracks(tracks.mapFilter(trackUriIfAlbum), releaseRadarNoSinglesId, accessToken)
      }

      _ <- info("Done!")
    } yield ()

  def mergePlaylistsProgram(
    refreshToken: RefreshToken,
    mergedPlaylistId: PlaylistId,
    playlistsToMerge: List[PlaylistId]
  ): RIO[MergePlaylistsProgramR, Unit] =
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

  def albumAnniversariesPlaylistProgram(
    refreshToken: RefreshToken,
    monthDay: Option[MonthDay]
  ): RIO[AlbumAnniversariesPlaylistProgramR, Unit] =
    for {
      accessToken <- requestAccessTokenProgram(refreshToken)
      myProfile   <- getMyProfile(GetMyProfileRequest(accessToken))
      md          <- monthDay.fold(clock.currentDateTime.map(MonthDay.from))(ZIO.succeed(_))

      playlistInfo = AnniversaryPlaylistInfo.fromMonthDay(md)
      createPlaylistReq = CreatePlaylistRequest.forAnniversaryPlaylist(accessToken, myProfile.id, playlistInfo)
      createPlaylistResp <- createPlaylist(createPlaylistReq)

      anniversaryPlaylistId = createPlaylistResp.id
      getMyFollowedArtistsReq = GetMyFollowedArtistsRequest.first(accessToken)
      _ <- paginateMyFollowedArtistsPar(getMyFollowedArtistsReq) { artistIds =>
        import GetArtistsAlbumsResponse.Album

        val makeSampleReq = GetAlbumSampleTrackRequest.make(accessToken, _)
        val maybeSampleReq = Some(_: Album).filter(isAnniversaryAlbum(_, md)).map(_.id).map(makeSampleReq)
        val getSampleTrackUri = getAlbumSampleTrack(_: GetAlbumSampleTrackRequest).map(TrackUri.fromTrackId)
        val sampleReqs = (_: List[Album]).mapFilter(maybeSampleReq)
        val sampleTrackUris = ZIO.foreachPar(_: List[GetAlbumSampleTrackRequest])(getSampleTrackUri)
        val getSampleTrackUrisFromAlbums = sampleReqs andThen sampleTrackUris
        val importSampleTracks = importTracks(_: List[TrackUri], anniversaryPlaylistId, accessToken)
        val importSampleTracksFromAlbums = getSampleTrackUrisFromAlbums(_: List[Album]).flatMap(importSampleTracks)

        val getArtistsAlbumsReqs = artistIds.map(GetArtistsAlbumsRequest.first(accessToken, _))
        ZIO.foreachPar_(getArtistsAlbumsReqs)(paginateArtistsAlbumsPar(_)(importSampleTracksFromAlbums))
      }
    } yield ()

  private def paginatePlaylistPar[R <: GetPlaylistItemsServiceR](
    req: GetPlaylistsItemsRequest[First]
  )(f: List[TrackResponse] => RIO[R, Unit]): RIO[R, Unit] =
    paginate(unitRIO[R])(fetchPlaylistItemsPage[R])((rio, items) => rio &> f(items))(req)

  private type ClearPlaylistR = GetPlaylistItemsServiceR with RemoveItemsFromPlaylistServiceR
  private def clearPlaylist[R <: ClearPlaylistR](req: GetPlaylistsItemsRequest[First]): RIO[R, Unit] =
    // As we are deleting tracks, we should always stay in the first page
    // and compose the effects sequentially
    paginate(unitRIO[R])(fetchPlaylistItemsFixedPage[R, First])((rio, items) => rio *> deleteTracks(items, req))(req)

  private def importTracks[R <: AddItemsToPlaylistServiceR](
    trackUris: List[TrackUri],
    destPlaylist: PlaylistId,
    accessToken: AccessToken
  ): RIO[R, Unit] = {
    val iterable =
      trackUris
        .to(LazyList)
        .grouped(PlaylistItemsToProcess.MaxSize)
        .map(_.toVector)
        .map(refineRIO[AddItemsToPlaylistServiceR, PlaylistItemsToProcessP](_))
        .map(_.flatMap(importTrackChunk(_, destPlaylist, accessToken)))
        .to(Iterable)

    ZIO.foreachPar_(iterable)(identity)
  }

  private def deleteTracks[R <: RemoveItemsFromPlaylistServiceR](
    items: List[TrackResponse],
    req: GetPlaylistsItemsRequest[First]
  ): RIO[R, Unit] = {
    val iterable =
      items
        .to(LazyList)
        .map(_.uri)
        .grouped(PlaylistItemsToProcess.MaxSize)
        .map(_.toVector)
        .map(refineRIO[RemoveItemsFromPlaylistServiceR, PlaylistItemsToProcessP](_))
        .map(_.map(RemoveItemsFromPlaylistRequest.make(_, req.requestType.playlistId, req.accessToken)))
        .map(_.flatMap(removeItemsFromPlaylist))
        .to(Iterable)

    ZIO.foreachPar_(iterable)(identity)
  }

  private def importTrackChunk[R <: AddItemsToPlaylistServiceR](
    trackUris: PlaylistItemsToProcess[TrackUri],
    destPlaylist: PlaylistId,
    accessToken: AccessToken
  ): RIO[R, Unit] =
    addItemsToPlaylist(AddItemsToPlaylistRequest.make(accessToken, destPlaylist, trackUris)).map(_ => ())

  private type MergePlaylistsR = ImportPlaylistR with LogR
  private def mergePlaylists[R <: MergePlaylistsR](
    sources: List[PlaylistId],
    dest: PlaylistId,
    mkReq: PlaylistId => GetPlaylistsItemsRequest[First],
    retry: RetryConfig
  ): RIO[R, Unit] =
    ZIO.foreachPar_(sources) { playlist =>
      info(show"> playlist($playlist) is being imported") *>
        importPlaylist(mkReq(playlist), dest, retry) *>
        info(show"< playlist($playlist) done")
    }

  private type ImportPlaylistR = GetPlaylistItemsServiceR with AddItemsToPlaylistServiceR with Clock
  private def importPlaylist[R <: ImportPlaylistR](
    source: GetPlaylistsItemsRequest[First],
    dest: PlaylistId,
    retry: RetryConfig
  ): RIO[R, Unit] =
    paginatePlaylistPar(source) { tracks =>
      importTracks(tracks.map(_.uri), dest, source.accessToken)
    }.retry(Schedule.exponential(Duration.fromScala(retry.retryAfter)) && Schedule.recurs(retry.attempts))

  private def fetchPlaylistItemsPage[R <: GetPlaylistItemsServiceR](
    req: GetPlaylistsItemsRequest[_]
  ): PageRIO[R, TrackResponse, GetPlaylistsItemsRequest[_]] =
    getPlaylistItems(req).map(getPlaylistItemsPage(req, _))

  private def fetchPlaylistItemsFixedPage[R <: GetPlaylistItemsServiceR, T <: GetPlaylistsItemsRequest.RequestType](
    req: GetPlaylistsItemsRequest[T]
  ): PageRIO[R, TrackResponse, GetPlaylistsItemsRequest[T]] =
    getPlaylistItems(req).map(getPlaylistItemsFixedPage[T](req, _))
}
