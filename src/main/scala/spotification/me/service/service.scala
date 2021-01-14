package spotification.me

import zio.{Has, RIO, Task, ZIO}

package object service {
  type GetMyProfileService = GetMyProfileRequest => Task[GetMyProfileResponse]
  type GetMyProfileServiceR = Has[GetMyProfileService]

  type GetMyFollowedArtistsService = GetMyFollowedArtistsRequest[_] => Task[GetMyFollowedArtistsResponse]
  type GetMyFollowedArtistsServiceR = Has[GetMyFollowedArtistsService]

  def getMyProfile(req: GetMyProfileRequest): RIO[GetMyProfileServiceR, GetMyProfileResponse] =
    ZIO.accessM(_.get.apply(req))

  def getMyFollowedArtists(
    req: GetMyFollowedArtistsRequest[_]
  ): RIO[GetMyFollowedArtistsServiceR, GetMyFollowedArtistsResponse] =
    ZIO.accessM(_.get.apply(req))
}
