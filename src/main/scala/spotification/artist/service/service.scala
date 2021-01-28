package spotification.artist

import spotification.effect.accessServiceFunction
import zio.{Has, RIO, Task}

package object service {
  type GetMyFollowedArtistsService = GetMyFollowedArtistsRequest[_] => Task[GetMyFollowedArtistsResponse]
  type GetMyFollowedArtistsServiceR = Has[GetMyFollowedArtistsService]

  type GetArtistsAlbumsService = GetArtistsAlbumsRequest[_] => Task[GetArtistsAlbumsResponse]
  type GetArtistsAlbumsServiceR = Has[GetArtistsAlbumsService]

  def getMyFollowedArtists(
    req: GetMyFollowedArtistsRequest[_]
  ): RIO[GetMyFollowedArtistsServiceR, GetMyFollowedArtistsResponse] =
    accessServiceFunction(req)

  def getArtistsAlbums(req: GetArtistsAlbumsRequest[_]): RIO[GetArtistsAlbumsServiceR, GetArtistsAlbumsResponse] =
    accessServiceFunction(req)
}
