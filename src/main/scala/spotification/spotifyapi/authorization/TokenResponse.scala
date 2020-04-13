package spotification.spotifyapi.authorization

import spotification.spotifyapi.PositiveInt

final case class TokenResponse(
  accessToken: AccessToken,
  refreshToken: RefreshToken,
  expiresIn: PositiveInt,
  scopes: List[Scope]
)
