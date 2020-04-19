package spotification.domain.spotify.authorization

import spotification.domain.RefreshTokenGrantType

final case class RefreshTokenRequest(
  client_id: ClientId,
  client_secret: ClientSecret,
  grant_type: RefreshTokenGrantType,
  refresh_token: RefreshToken
)
