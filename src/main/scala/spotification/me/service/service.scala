package spotification.me

import zio.{Has, RIO, ZIO}

package object service {
  type MeServiceEnv = Has[MeService]

  def getMyProfile(req: GetMyProfileRequest): RIO[MeServiceEnv, GetMyProfileResponse] =
    ZIO.accessM(_.get.getMyProfile(req))

  def getMyFollowedArtists(req: GetMyFollowedArtistsRequest[_]): RIO[MeServiceEnv, GetMyFollowedArtistsResponse] =
    ZIO.accessM(_.get.getMyFollowedArtists(req))
}
