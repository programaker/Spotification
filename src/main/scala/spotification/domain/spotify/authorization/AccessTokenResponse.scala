package spotification.domain.spotify.authorization

import spotification.domain.{PositiveInt, SpaceSeparatedString, TokenType}

final case class AccessTokenResponse(
  access_token: AccessToken,
  refresh_token: RefreshToken,
  expires_in: PositiveInt,
  scope: Option[SpaceSeparatedString],
  token_type: TokenType
)
