package spotification.core.spotify.authorization

import spotification.core._

final case class AccessTokenResponse(
  access_token: AccessToken,
  refresh_token: RefreshToken,
  expires_in: PositiveInt,
  scope: Option[SpaceSeparatedString],
  token_type: TokenType
)
