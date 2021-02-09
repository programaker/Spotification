package spotification.user

import spotification.effect.accessServiceFunction
import zio.{Has, RIO, Task}

package object service {
  type GetMyProfileService = GetMyProfileRequest => Task[GetMyProfileResponse]
  type GetMyProfileServiceR = Has[GetMyProfileService]
  def getMyProfile(req: GetMyProfileRequest): RIO[GetMyProfileServiceR, GetMyProfileResponse] =
    accessServiceFunction(req)
}
