package spotification.domain.spotify.authorization

import spotification.domain.{PositiveInt, TokenType}

final case class RefreshTokenResponse(
  access_token: AccessToken,
  token_type: TokenType,
  expires_in: PositiveInt,
  scope: Option[String]
)
