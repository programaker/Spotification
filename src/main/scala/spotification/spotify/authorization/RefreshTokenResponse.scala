package spotification.spotify.authorization

import spotification.PositiveInt

final case class RefreshTokenResponse(
  accessToken: AccessToken,
  expiresIn: PositiveInt,
  scopes: List[Scope]
)
