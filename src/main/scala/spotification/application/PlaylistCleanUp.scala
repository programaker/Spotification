package spotification.application

import spotification.domain.PositiveInt
import spotification.domain.spotify.authorization.AccessToken
import spotification.domain.spotify.playlist._
import spotification.infra.Infra.refineRIO
import spotification.infra.spotify.playlist.PlaylistModule
import zio.{RIO, ZIO}

object PlaylistCleanUp {
  def clearPlaylist(
    playlistId: PlaylistId,
    accessToken: AccessToken,
    limit: PositiveInt
  ): RIO[PlaylistModule, Unit] =
    PlaylistPagination.foreachPage(playlistId, limit, accessToken) { items =>
      ZIO.foreachPar_(
        items
          .to(LazyList)
          .map(_.uri)
          .grouped(PlaylistItemsToProcess.MaxSize)
          .map(_.toVector)
          .map(refineRIO[PlaylistModule, PlaylistItemsToProcessR](_))
          .map(_.map(RemoveItemsFromPlaylistRequest.make(accessToken, playlistId, _)))
          .map(_.flatMap(PlaylistModule.removeItemsFromPlaylist))
          .to(Iterable)
      )(identity)
    }
}
