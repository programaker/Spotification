package spotification.me

import zio.{Has, RIO, Task, ZIO}

package object service {
  type GetMyFollowedArtistsService = GetMyFollowedArtistsRequest[_] => Task[GetMyFollowedArtistsResponse]
  type GetMyFollowedArtistsServiceR = Has[GetMyFollowedArtistsService]

  def getMyFollowedArtists(
    req: GetMyFollowedArtistsRequest[_]
  ): RIO[GetMyFollowedArtistsServiceR, GetMyFollowedArtistsResponse] =
    ZIO.accessM(_.get.apply(req))
}
