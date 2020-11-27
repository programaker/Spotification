package spotification.user.service

import spotification.user.{GetMyProfileRequest, GetMyProfileResponse}
import zio.Task

trait UserService {
  def getMyProfile(req: GetMyProfileRequest): Task[GetMyProfileResponse]
}
