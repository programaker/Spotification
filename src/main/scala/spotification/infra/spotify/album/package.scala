package spotification.infra.spotify

import spotification.domain.config.AlbumConfig
import spotification.domain.spotify.album.{GetSeveralAlbumsRequest, GetSeveralAlbumsResponse}
import spotification.infra.config.AlbumConfigModule
import spotification.infra.httpclient.{H4sAlbumService, H4sClient, HttpClientModule}
import zio.{Has, RIO, Task, TaskLayer, ZIO, ZLayer}

package object album {
  type AlbumModule = Has[AlbumModule.Service]
  object AlbumModule {
    def getSeveralAlbums(req: GetSeveralAlbumsRequest): RIO[AlbumModule, GetSeveralAlbumsResponse] =
      ZIO.accessM(_.get.getSeveralAlbums(req))

    val layer: TaskLayer[AlbumModule] = {
      val l1 = ZLayer.fromServices[AlbumConfig, H4sClient, AlbumModule.Service] { (config, httpClient) =>
        new H4sAlbumService(config.albumApiUri, httpClient)
      }

      (AlbumConfigModule.layer ++ HttpClientModule.layer) >>> l1
    }

    trait Service {
      def getSeveralAlbums(req: GetSeveralAlbumsRequest): Task[GetSeveralAlbumsResponse]
    }
  }
}
