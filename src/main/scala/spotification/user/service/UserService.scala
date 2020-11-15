package spotification.user.service

import spotification.user.{GetCurrentUserProfileRequest, GetCurrentUserProfileResponse}
import zio.Task

trait UserService {
  def getCurrentUserProfile(req: GetCurrentUserProfileRequest): Task[GetCurrentUserProfileResponse]
}
