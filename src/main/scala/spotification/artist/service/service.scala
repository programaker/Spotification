package spotification.artist

import zio.{Has, RIO, Task, ZIO}

package object service {
  type GetArtistsAlbumsService = GetArtistsAlbumsRequest[_] => Task[GetArtistsAlbumsResponse]
  type GetArtistsAlbumsServiceEnv = Has[GetArtistsAlbumsService]

  def getArtistsAlbums(req: GetArtistsAlbumsRequest[_]): RIO[GetArtistsAlbumsServiceEnv, GetArtistsAlbumsResponse] =
    ZIO.accessM(_.get.apply(req))
}
