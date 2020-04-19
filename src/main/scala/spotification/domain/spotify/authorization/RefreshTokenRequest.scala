package spotification.domain.spotify.authorization

final case class RefreshTokenRequest(
  client_id: ClientId,
  client_secret: ClientSecret,
  refresh_token: RefreshToken
)
