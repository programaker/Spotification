package spotification.spotify.authorization.domain

import spotification.common.domain.PositiveInt

final case class RefreshTokenResponse(
  access_token: AccessToken,
  token_type: TokenType,
  expires_in: PositiveInt,
  scope: Option[String]
)
