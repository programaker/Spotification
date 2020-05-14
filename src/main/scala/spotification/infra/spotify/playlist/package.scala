package spotification.infra.spotify

import spotification.domain.config.PlaylistConfig
import spotification.domain.spotify.playlist.{
  AddItemsToPlaylistRequest,
  AddItemsToPlaylistResponse,
  PlaylistItemsRequest,
  PlaylistItemsResponse
}
import spotification.infra.config.PlaylistConfigModule
import spotification.infra.httpclient.{H4sClient, H4sPlaylistService, HttpClientModule}
import zio._

package object playlist {
  type PlaylistModule = Has[PlaylistModule.Service]
  object PlaylistModule {
    def getPlaylistItems(req: PlaylistItemsRequest): RIO[PlaylistModule, PlaylistItemsResponse] =
      ZIO.accessM(_.get.getPlaylistItems(req))

    val layer: TaskLayer[PlaylistModule] = {
      val l1 = ZLayer.fromServices[PlaylistConfig, H4sClient, PlaylistModule.Service] { (config, httpClient) =>
        new H4sPlaylistService(config.playlistApiUri, httpClient)
      }

      (PlaylistConfigModule.layer ++ HttpClientModule.layer) >>> l1
    }

    trait Service {
      def getPlaylistItems(req: PlaylistItemsRequest): Task[PlaylistItemsResponse]
      def addItemsToPlaylist(req: AddItemsToPlaylistRequest): Task[AddItemsToPlaylistResponse]
    }
  }
}
