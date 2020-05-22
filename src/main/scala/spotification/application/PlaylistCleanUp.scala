package spotification.application

import spotification.domain.spotify.authorization.AccessToken
import spotification.domain.spotify.playlist.{
  GetPlaylistsItemsRequest,
  PlaylistId,
  PlaylistItemsToProcess,
  PlaylistItemsToProcessR,
  RemoveItemsFromPlaylistRequest
}
import spotification.infra.config.PlaylistConfigModule
import spotification.infra.spotify.playlist.PlaylistModule
import zio.{RIO, ZIO}
import eu.timepit.refined.auto._
import spotification.domain.spotify.playlist.GetPlaylistsItemsRequest.{FirstRequest, NextRequest}
import spotification.infra.Infra.refineRIO

object PlaylistCleanUp {
  type Env = PlaylistModule with PlaylistConfigModule

  def clearPlaylist(playlistId: PlaylistId, accessToken: AccessToken): RIO[Env, Unit] =
    for {
      playlistConfig <- PlaylistConfigModule.config

      req = FirstRequest(
        accessToken = accessToken,
        playlistId = playlistId,
        limit = playlistConfig.getPlaylistItemsLimit,
        offset = 0
      )

      _ <- removeItemsFromPlaylist(playlistId, req)
    } yield ()

  private def removeItemsFromPlaylist(playlistId: PlaylistId, req: GetPlaylistsItemsRequest): RIO[Env, Unit] =
    PlaylistModule.getPlaylistItems(req).flatMap { resp =>
      val accessToken = GetPlaylistsItemsRequest.accessToken(req)
      val trackUris = resp.items.map(_.uri)

      val removeItems = ZIO.foreachPar_ {
        trackUris
          .to(LazyList)
          .grouped(PlaylistItemsToProcess.MaxSize)
          .map(_.toVector)
          .map(refineRIO[PlaylistModule, PlaylistItemsToProcessR](_))
          .map(_.map(RemoveItemsFromPlaylistRequest.make(accessToken, playlistId, _)))
          .map(_.flatMap(PlaylistModule.removeItemsFromPlaylist))
          .to(Iterable)
      }(identity)

      val nextPage = resp.next match {
        case None      => RIO.unit
        case Some(uri) => removeItemsFromPlaylist(playlistId, NextRequest(accessToken, uri))
      }

      removeItems.zipParRight(nextPage)
    }
}
