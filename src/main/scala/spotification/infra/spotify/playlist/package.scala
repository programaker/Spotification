package spotification.infra.spotify

import spotification.domain.spotify.playlist.{PlaylistItemsRequest, PlaylistItemsResponse}
import spotification.infra.BaseEnv
import spotification.infra.httpclient.{H4sPlaylistService, HttpClientModule}
import zio._

package object playlist {
  type PlaylistModule = Has[PlaylistModule.Service]
  object PlaylistModule {
    val layer: TaskLayer[PlaylistModule] =
      HttpClientModule.layer >>> ZLayer.fromService(new H4sPlaylistService(_))

    def getPlaylistItems(req: PlaylistItemsRequest): RIO[PlaylistModule with BaseEnv, PlaylistItemsResponse] =
      ZIO.accessM(_.get.getPlaylistItems(req))

    trait Service {
      def getPlaylistItems(req: PlaylistItemsRequest): RIO[BaseEnv, PlaylistItemsResponse]
    }
  }
}
