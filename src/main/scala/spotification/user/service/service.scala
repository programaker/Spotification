package spotification.user

import zio.{Has, RIO, Task, ZIO}

package object service {
  type GetMyProfileService = GetMyProfileRequest => Task[GetMyProfileResponse]
  type GetMyProfileServiceR = Has[GetMyProfileService]

  def getMyProfile(req: GetMyProfileRequest): RIO[GetMyProfileServiceR, GetMyProfileResponse] =
    ZIO.accessM(_.get.apply(req))
}
