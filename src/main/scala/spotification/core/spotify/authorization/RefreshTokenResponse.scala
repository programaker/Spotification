package spotification.core.spotify.authorization

import spotification.core.PositiveInt

final case class RefreshTokenResponse(
  access_token: AccessToken,
  token_type: TokenType,
  expires_in: PositiveInt,
  scope: Option[String]
)
