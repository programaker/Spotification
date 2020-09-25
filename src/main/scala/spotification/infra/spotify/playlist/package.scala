package spotification.infra.spotify

import spotification.domain.config.PlaylistConfig
import spotification.domain.spotify.playlist._
import spotification.infra.config.PlaylistConfigModule
import spotification.infra.httpclient._
import zio._

package object playlist {
  type PlaylistModule = Has[PlaylistService]
  object PlaylistModule {
    def getPlaylistItems(req: GetPlaylistsItemsRequest): RIO[PlaylistModule, GetPlaylistsItemsResponse] =
      ZIO.accessM(_.get.getPlaylistsItems(req))

    def addItemsToPlaylist(req: AddItemsToPlaylistRequest): RIO[PlaylistModule, PlaylistSnapshotResponse] =
      ZIO.accessM(_.get.addItemsToPlaylist(req))

    def removeItemsFromPlaylist(
      req: RemoveItemsFromPlaylistRequest
    ): RIO[PlaylistModule, PlaylistSnapshotResponse] =
      ZIO.accessM(_.get.removeItemsFromPlaylist(req))

    val live: TaskLayer[PlaylistModule] = {
      val l1 = ZLayer.fromServices[PlaylistConfig, H4sClient, PlaylistService] { (config, httpClient) =>
        new H4sPlaylistService(config.playlistApiUri, httpClient)
      }

      (PlaylistConfigModule.live ++ HttpClientModule.live) >>> l1
    }
  }
}
