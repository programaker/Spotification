package spotification.domain.spotify.authorization

import spotification.domain.PositiveInt

final case class RefreshTokenResponse(
  access_token: AccessToken,
  token_type: String,
  expires_in: PositiveInt,
  scope: Option[String]
)
