package spotification.spotify.authorization

import spotification.spotify.PositiveInt

final case class RefreshTokenResponse(
  accessToken: AccessToken,
  expiresIn: PositiveInt,
  scopes: List[Scope]
)
