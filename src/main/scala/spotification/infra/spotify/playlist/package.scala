package spotification.infra.spotify

import spotification.domain.config.PlaylistConfig
import spotification.domain.spotify.playlist.{PlaylistItemsRequest, PlaylistItemsResponse}
import spotification.infra.BaseEnv
import spotification.infra.config.PlaylistConfigModule
import spotification.infra.httpclient.{H4sClient, H4sPlaylistService, HttpClientModule}
import zio._

package object playlist {
  type PlaylistModule = Has[PlaylistModule.Service]
  object PlaylistModule {
    def getPlaylistItems(req: PlaylistItemsRequest): RIO[PlaylistModule with BaseEnv, PlaylistItemsResponse] =
      ZIO.accessM(_.get.getPlaylistItems(req))

    val layer: TaskLayer[PlaylistModule] = {
      val l1 = ZLayer.fromServices[PlaylistConfig, H4sClient, PlaylistModule.Service] { (config, httpClient) =>
        new H4sPlaylistService(config.playlistApiUri, httpClient)
      }

      (PlaylistConfigModule.layer ++ HttpClientModule.layer) >>> l1
    }

    trait Service {
      def getPlaylistItems(req: PlaylistItemsRequest): RIO[BaseEnv, PlaylistItemsResponse]
    }
  }
}
