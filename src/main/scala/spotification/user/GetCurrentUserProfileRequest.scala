package spotification.user

import spotification.authorization.AccessToken

final case class GetCurrentUserProfileRequest(accessToken: AccessToken)
