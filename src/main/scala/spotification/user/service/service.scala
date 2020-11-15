package spotification.user

import zio.{Has, RIO, ZIO}

package object service {
  type UserServiceEnv = Has[UserService]

  def getCurrentUserProfile(req: GetCurrentUserProfileRequest): RIO[UserServiceEnv, GetCurrentUserProfileResponse] =
    ZIO.accessM(_.get.getCurrentUserProfile(req))
}
