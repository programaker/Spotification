package spotification.spotify.authorization

import spotification.spotify.PositiveInt

final case class AccessTokenResponse(
  access_token: AccessToken,
  refresh_token: RefreshToken,
  expires_in: PositiveInt,
  scope: String,
  token_type: String
)
