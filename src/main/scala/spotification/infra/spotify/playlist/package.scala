package spotification.infra.spotify

import spotification.domain.config.PlaylistConfig
import spotification.domain.spotify.playlist.{
  AddItemsToPlaylistRequest,
  AddItemsToPlaylistResponse,
  GetPlaylistsItemsRequest,
  GetPlaylistsItemsResponse
}
import spotification.infra.config.PlaylistConfigModule
import spotification.infra.httpclient.{H4sClient, H4sPlaylistService, HttpClientModule}
import zio._

package object playlist {
  type PlaylistModule = Has[PlaylistModule.Service]
  object PlaylistModule {
    def getPlaylistItems(req: GetPlaylistsItemsRequest): RIO[PlaylistModule, GetPlaylistsItemsResponse] =
      ZIO.accessM(_.get.getPlaylistsItems(req))

    def addItemsToPlaylist(req: AddItemsToPlaylistRequest): RIO[PlaylistModule, AddItemsToPlaylistResponse] =
      ZIO.accessM(_.get.addItemsToPlaylist(req))

    val layer: TaskLayer[PlaylistModule] = {
      val l1 = ZLayer.fromServices[PlaylistConfig, H4sClient, PlaylistModule.Service] { (config, httpClient) =>
        new H4sPlaylistService(config.playlistApiUri, httpClient)
      }

      (PlaylistConfigModule.layer ++ HttpClientModule.layer) >>> l1
    }

    trait Service {
      def getPlaylistsItems(req: GetPlaylistsItemsRequest): Task[GetPlaylistsItemsResponse]
      def addItemsToPlaylist(req: AddItemsToPlaylistRequest): Task[AddItemsToPlaylistResponse]
    }
  }
}
