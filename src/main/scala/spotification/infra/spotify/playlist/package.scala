package spotification.infra.spotify

import spotification.domain.spotify.playlist.{PlaylistItemsRequest, PlaylistItemsResponse}
import spotification.infra.BaseEnv
import spotification.infra.httpclient.HttpClientModule
import zio.{Has, RIO, URLayer, ZIO}

package object playlist {
  type PlaylistModule = Has[PlaylistModule.Service]
  object PlaylistModule {
    val layer: URLayer[HttpClientModule, PlaylistModule] = ???

    def getPlaylistItems(req: PlaylistItemsRequest): RIO[PlaylistModule with BaseEnv, PlaylistItemsResponse] =
      ZIO.accessM(_.get.getPlaylistItems(req))

    trait Service {
      def getPlaylistItems(req: PlaylistItemsRequest): RIO[BaseEnv, PlaylistItemsResponse]
    }
  }
}
