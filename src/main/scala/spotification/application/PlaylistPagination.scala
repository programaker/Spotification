package spotification.application

import spotification.domain.{CurrentUri, NextUri, UriString}
import spotification.domain.spotify.playlist.GetPlaylistsItemsRequest.NextRequest
import spotification.domain.spotify.playlist.GetPlaylistsItemsResponse.Success.TrackResponse
import spotification.domain.spotify.playlist.GetPlaylistsItemsRequest
import spotification.infra.spotify.playlist.PlaylistModule
import zio.RIO

object PlaylistPagination {
  def foreachPagePar[R <: PlaylistModule](req: GetPlaylistsItemsRequest.FirstRequest)(
    f: List[TrackResponse] => RIO[R, Unit]
  ): RIO[R, Unit] =
    foreachPage(req)(f)((_, nextUri) => nextUri)(_ &> _)

  /** Uses GetPlaylistsItemsRequest to fetch items from a playlist with pagination.
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
    processTracks: List[TrackResponse] => RIO[R, Unit]
  )(
    nextPageUri: (CurrentUri, NextUri) => UriString
  )(
    combinePageEffects: (RIO[R, Unit], RIO[R, Unit]) => RIO[R, Unit]
  ): RIO[R, Unit] = {
    def loop(req: GetPlaylistsItemsRequest): RIO[R, Unit] =
      PlaylistModule.getPlaylistItems(req).flatMap { resp =>
        val thisPage = processTracks(resp.items.map(_.track))

        val nextPage = resp.next match {
          case None =>
            RIO.unit
          case Some(nextUri) =>
            loop(NextRequest(GetPlaylistsItemsRequest.accessToken(req), nextPageUri(resp.href, nextUri)))
        }

        combinePageEffects(thisPage, nextPage)
      }

    loop(req)
  }
}
