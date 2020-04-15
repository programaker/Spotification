package spotification.spotify.authorization

import spotification.spotify.PositiveInt

final case class RefreshTokenResponse(
  access_token: AccessToken,
  token_type: String,
  expires_in: PositiveInt,
  scope: String
)
