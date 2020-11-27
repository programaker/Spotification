package spotification.user

import zio.{Has, RIO, ZIO}

package object service {
  type UserServiceEnv = Has[UserService]

  def getMyProfile(req: GetMyProfileRequest): RIO[UserServiceEnv, GetMyProfileResponse] =
    ZIO.accessM(_.get.getMyProfile(req))
}
