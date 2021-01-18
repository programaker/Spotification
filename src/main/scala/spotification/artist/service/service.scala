package spotification.artist

import zio.{Has, RIO, Task, ZIO}

package object service {
  type GetMyFollowedArtistsService = GetMyFollowedArtistsRequest[_] => Task[GetMyFollowedArtistsResponse]
  type GetMyFollowedArtistsServiceR = Has[GetMyFollowedArtistsService]

  type GetArtistsAlbumsService = GetArtistsAlbumsRequest[_] => Task[GetArtistsAlbumsResponse]
  type GetArtistsAlbumsServiceR = Has[GetArtistsAlbumsService]

  def getMyFollowedArtists(
    req: GetMyFollowedArtistsRequest[_]
  ): RIO[GetMyFollowedArtistsServiceR, GetMyFollowedArtistsResponse] =
    ZIO.accessM(_.get.apply(req))

  def getArtistsAlbums(req: GetArtistsAlbumsRequest[_]): RIO[GetArtistsAlbumsServiceR, GetArtistsAlbumsResponse] =
    ZIO.accessM(_.get.apply(req))
}
