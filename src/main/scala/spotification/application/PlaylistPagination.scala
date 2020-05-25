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
  def foreachPage[R <: PlaylistModule](playlistId: PlaylistId, limit: PositiveInt, accessToken: AccessToken)(
    f: List[TrackResponse] => RIO[R, Unit]
  ): RIO[R, Unit] =
    foreachPageCombining(playlistId, limit, accessToken)(f)(_ *> _)

  def foreachPagePar[R <: PlaylistModule](playlistId: PlaylistId, limit: PositiveInt, accessToken: AccessToken)(
    f: List[TrackResponse] => RIO[R, Unit]
  ): RIO[R, Unit] =
    foreachPageCombining(playlistId, limit, accessToken)(f)(_ &> _)

  private def foreachPageCombining[R <: PlaylistModule](
    playlistId: PlaylistId,
    limit: PositiveInt,
    accessToken: AccessToken
  )(
    f: List[TrackResponse] => RIO[R, Unit]
  )(
    combine: (RIO[R, Unit], RIO[R, Unit]) => RIO[R, Unit]
  ): RIO[R, Unit] = {
    def loop(req: GetPlaylistsItemsRequest): RIO[R, Unit] =
      PlaylistModule.getPlaylistItems(req).flatMap { resp =>
        val thisPage = f(resp.items.map(_.track))

        val nextPage = resp.next match {
          case None      => RIO.unit
          case Some(uri) => loop(NextRequest(accessToken, uri))
        }

        combine(thisPage, nextPage)
      }

    loop(FirstRequest(accessToken, playlistId, limit, offset = 0))
  }
}
