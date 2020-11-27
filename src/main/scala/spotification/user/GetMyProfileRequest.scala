package spotification.user

import spotification.authorization.AccessToken

final case class GetMyProfileRequest(accessToken: AccessToken)
