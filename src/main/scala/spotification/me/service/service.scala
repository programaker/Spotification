package spotification.me

import zio.{Has, RIO, Task, ZIO}

package object service {
  type GetMyProfileService = GetMyProfileRequest => Task[GetMyProfileResponse]
  type GetMyProfileServiceEnv = Has[GetMyProfileService]
  def getMyProfile(req: GetMyProfileRequest): RIO[GetMyProfileServiceEnv, GetMyProfileResponse] =
    ZIO.accessM(_.get.apply(req))

  type GetMyFollowedArtistsService = GetMyFollowedArtistsRequest[_] => Task[GetMyFollowedArtistsResponse]
  type GetMyFollowedArtistsServiceEnv = Has[GetMyFollowedArtistsService]
  def getMyFollowedArtists(
    req: GetMyFollowedArtistsRequest[_]
  ): RIO[GetMyFollowedArtistsServiceEnv, GetMyFollowedArtistsResponse] =
    ZIO.accessM(_.get.apply(req))
}
