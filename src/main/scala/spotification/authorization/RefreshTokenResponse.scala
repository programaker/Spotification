package spotification.authorization

import spotification.common.PositiveInt

final case class RefreshTokenResponse(
  access_token: AccessToken,
  token_type: TokenType,
  expires_in: PositiveInt,
  scope: Option[String]
)
