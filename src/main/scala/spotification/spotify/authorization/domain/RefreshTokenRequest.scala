package spotification.spotify.authorization.domain

final case class RefreshTokenRequest(
  client_id: ClientId,
  client_secret: ClientSecret,
  grant_type: RefreshTokenGrantType,
  refresh_token: RefreshToken
)
