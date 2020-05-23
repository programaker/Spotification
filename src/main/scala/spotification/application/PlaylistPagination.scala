package spotification.application

import eu.timepit.refined.auto._
import spotification.domain.PositiveInt
import spotification.domain.spotify.authorization.AccessToken
import spotification.domain.spotify.playlist.GetPlaylistsItemsRequest.{FirstRequest, NextRequest}
import spotification.domain.spotify.playlist.GetPlaylistsItemsResponse.Success.TrackResponse
import spotification.domain.spotify.playlist.{GetPlaylistsItemsRequest, PlaylistId}
import spotification.infra.spotify.playlist.PlaylistModule
import zio.RIO

object PlaylistPagination {
  def foreachPage[R <: PlaylistModule](playlistId: PlaylistId, accessToken: AccessToken, limit: PositiveInt)(
    f: List[TrackResponse] => RIO[R, Unit]
  ): RIO[R, Unit] = {
    def loop(req: GetPlaylistsItemsRequest): RIO[R, Unit] =
      PlaylistModule.getPlaylistItems(req).flatMap { resp =>
        val thisPage = f(resp.items)

        val nextPage = resp.next match {
          case None      => RIO.unit
          case Some(uri) => loop(NextRequest(accessToken, uri))
        }

        thisPage.zipParRight(nextPage)
      }

    loop(FirstRequest(accessToken, playlistId, limit, offset = 0))
  }
}
