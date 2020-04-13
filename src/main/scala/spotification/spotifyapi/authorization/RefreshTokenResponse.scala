package spotification.spotifyapi.authorization

import spotification.spotifyapi.PositiveInt

final case class RefreshTokenResponse(
  accessToken: AccessToken,
  expiresIn: PositiveInt,
  scopes: List[Scope]
)
