package spotification.spotify.authorization

import spotification.PositiveInt

final case class TokenResponse(
  accessToken: AccessToken,
  refreshToken: RefreshToken,
  expiresIn: PositiveInt,
  scopes: List[Scope]
)
