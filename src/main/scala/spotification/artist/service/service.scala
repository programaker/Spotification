package spotification.artist

import spotification.effect.accessServiceFunction
import zio.{Has, RIO, Task}

package object service {
  type GetMyFollowedArtistsService = GetMyFollowedArtistsRequest[_] => Task[GetMyFollowedArtistsResponse]
  type GetMyFollowedArtistsServiceR = Has[GetMyFollowedArtistsService]
  def getMyFollowedArtists(
    req: GetMyFollowedArtistsRequest[_]
  ): RIO[GetMyFollowedArtistsServiceR, GetMyFollowedArtistsResponse] =
    accessServiceFunction(req)

  type GetArtistsAlbumsService = GetArtistsAlbumsRequest[_] => Task[GetArtistsAlbumsResponse]
  type GetArtistsAlbumsServiceR = Has[GetArtistsAlbumsService]
  def getArtistsAlbums(req: GetArtistsAlbumsRequest[_]): RIO[GetArtistsAlbumsServiceR, GetArtistsAlbumsResponse] =
    accessServiceFunction(req)
}
