package spotification.infra.spotify

import spotification.domain.spotify.album.{GetSeveralAlbumsRequest, GetSeveralAlbumsResponse}
import zio.{Has, RIO, Task, ZIO}

package object album {
  type AlbumModule = Has[AlbumModule.Service]
  object AlbumModule {
    def getSeveralAlbums(req: GetSeveralAlbumsRequest): RIO[AlbumModule, GetSeveralAlbumsResponse] =
      ZIO.accessM(_.get.getSeveralAlbums(req))

    trait Service {
      def getSeveralAlbums(req: GetSeveralAlbumsRequest): Task[GetSeveralAlbumsResponse]
    }
  }
}
