package spotification.domain.spotify.authorization

import spotification.domain.PositiveInt

final case class AccessTokenResponse(
  access_token: AccessToken,
  refresh_token: RefreshToken,
  expires_in: PositiveInt,
  scope: String,
  token_type: String
)
