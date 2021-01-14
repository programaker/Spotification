package spotification.artist

import zio.{Has, RIO, Task, ZIO}

package object service {
  type GetArtistsAlbumsService = GetArtistsAlbumsRequest[_] => Task[GetArtistsAlbumsResponse]
  type GetArtistsAlbumsServiceR = Has[GetArtistsAlbumsService]

  def getArtistsAlbums(req: GetArtistsAlbumsRequest[_]): RIO[GetArtistsAlbumsServiceR, GetArtistsAlbumsResponse] =
    ZIO.accessM(_.get.apply(req))
}
