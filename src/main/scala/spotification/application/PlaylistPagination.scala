package spotification.application

import spotification.domain.{CurrentUri, NextUri, UriString}
import spotification.domain.spotify.playlist.GetPlaylistsItemsRequest.NextRequest
import spotification.domain.spotify.playlist.GetPlaylistsItemsResponse.Success.TrackResponse
import spotification.domain.spotify.playlist.GetPlaylistsItemsRequest
import spotification.infra.spotify.playlist.PlaylistModule
import zio.RIO
import cats.data.NonEmptyList

object PlaylistPagination {
  def foreachPagePar[R <: PlaylistModule](req: GetPlaylistsItemsRequest.FirstRequest)(
    f: NonEmptyList[TrackResponse] => RIO[R, Unit]
  ): RIO[R, Unit] =
    foreachPage(req)(f)((_, nextUri) => nextUri)(_ &> _)

  /** Uses GetPlaylistsItemsRequest to fetch items from a playlist with pagination.
   * This function has a strange signature, but the tread-off is "unparalled type-inference"
   *
   * @param req The initial request
   * @param processTracks A function to process the tracks in a page
   *
   * @param nextPageUri A function to choose whether to go to the next page
   * or stay in the current (useful to delete tracks for instance)
   *
   * @param combinePageEffects A function to combine the result of processing
   * the current page with the result of the next page request (useful to choose if parallel or not)
   * */
  def foreachPage[R <: PlaylistModule](
    req: GetPlaylistsItemsRequest
  )(
    processTracks: NonEmptyList[TrackResponse] => RIO[R, Unit]
  )(
    chooseUri: (CurrentUri, NextUri) => UriString
  )(
    combinePageEffects: (RIO[R, Unit], RIO[R, Unit]) => RIO[R, Unit]
  ): RIO[R, Unit] = {
    def loop(req: GetPlaylistsItemsRequest): RIO[R, Unit] =
      PlaylistModule.getPlaylistItems(req).flatMap { resp =>
        val runit: RIO[R, Unit] = RIO.unit

        if (resp.items.isEmpty)
          runit
        else {
          val thisPage = NonEmptyList.fromList(resp.items.map(_.track)).fold(runit)(processTracks)

          val nextPage = resp.next match {
            case None =>
              runit
            case Some(nextUri) =>
              val accessToken = GetPlaylistsItemsRequest.accessToken(req)
              val uri = chooseUri(resp.href, nextUri)
              loop(NextRequest(accessToken, uri))
          }

          combinePageEffects(thisPage, nextPage)
        }
      }

    loop(req)
  }
}
