package spotification.spotify.authorization

import spotification.spotify.PositiveInt

final case class TokenResponse(
  accessToken: AccessToken,
  refreshToken: RefreshToken,
  expiresIn: PositiveInt,
  scopes: List[Scope]
)
