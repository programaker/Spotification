package spotification.domain.spotify.authorization

import spotification.domain.{NonBlankString, PositiveInt, TokenType}

final case class AccessTokenResponse(
  access_token: AccessToken,
  refresh_token: RefreshToken,
  expires_in: PositiveInt,
  scope: Option[NonBlankString],
  token_type: TokenType
)
