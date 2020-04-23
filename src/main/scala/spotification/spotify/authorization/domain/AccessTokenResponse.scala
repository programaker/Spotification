package spotification.spotify.authorization.domain

import spotification.common.domain.{PositiveInt, SpaceSeparatedString}

final case class AccessTokenResponse(
  access_token: AccessToken,
  refresh_token: RefreshToken,
  expires_in: PositiveInt,
  scope: Option[SpaceSeparatedString],
  token_type: TokenType
)
