package spotification.spotify.playlist

import cats.data.NonEmptyList
import spotification.application.deleteTracks
import spotification.config.PlaylistConfig
import spotification.spotify.playlist.GetPlaylistsItemsRequest.NextRequest
import spotification.spotify.playlist.GetPlaylistsItemsResponse.TrackResponse
import spotification.spotify.playlist.infra.H4sPlaylistService
import zio._

package object application {
  type PlaylistModule = Has[PlaylistService]
  object PlaylistModule {
    val live: TaskLayer[PlaylistModule] = {
      val l1 = ZLayer.fromServices[PlaylistConfig, H4sClient, PlaylistService] { (config, httpClient) =>
        new H4sPlaylistService(config.playlistApiUri, httpClient)
      }

      (PlaylistConfigModule.live ++ HttpClientModule.live) >>> l1
    }
  }

  def getPlaylistItems(req: GetPlaylistsItemsRequest): RIO[PlaylistModule, GetPlaylistsItemsResponse] =
    ZIO.accessM(_.get.getPlaylistsItems(req))

  def addItemsToPlaylist(req: AddItemsToPlaylistRequest): RIO[PlaylistModule, PlaylistSnapshotResponse] =
    ZIO.accessM(_.get.addItemsToPlaylist(req))

  def removeItemsFromPlaylist(req: RemoveItemsFromPlaylistRequest): RIO[PlaylistModule, PlaylistSnapshotResponse] =
    ZIO.accessM(_.get.removeItemsFromPlaylist(req))

  def paginatePlaylistPar[R <: PlaylistModule](req: GetPlaylistsItemsRequest.FirstRequest)(
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
  def paginatePlaylist[R <: PlaylistModule](
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

  def clearPlaylist(req: GetPlaylistsItemsRequest.FirstRequest): RIO[PlaylistModule, Unit] =
    // `(currentUri, _) => currentUri`:
    // since we are deleting tracks,
    // we should always stay in the first page
    paginatePlaylist(req)(deleteTracks(_, req))((currentUri, _) => currentUri)(_ *> _)
}
