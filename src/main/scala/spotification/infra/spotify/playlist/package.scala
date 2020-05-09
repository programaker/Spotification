package spotification.infra.spotify

import spotification.domain.spotify.playlist.{PlaylistItemsRequest, PlaylistItemsResponse}
import spotification.infra.BaseEnv
import spotification.infra.httpclient.{H4sPlaylistService, HttpClientModule}
import zio.{Has, RIO, URLayer, ZIO, ZLayer}

package object playlist {
  type PlaylistModule = Has[PlaylistModule.Service]
  object PlaylistModule {
    val layer: URLayer[HttpClientModule, PlaylistModule] =
      ZLayer.fromService(new H4sPlaylistService(_))

    def getPlaylistItems(req: PlaylistItemsRequest): RIO[PlaylistModule with BaseEnv, PlaylistItemsResponse] =
      ZIO.accessM(_.get.getPlaylistItems(req))

    trait Service {
      def getPlaylistItems(req: PlaylistItemsRequest): RIO[BaseEnv, PlaylistItemsResponse]
    }
  }
}
