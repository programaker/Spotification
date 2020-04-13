package spotification.spotifyapi.authorization

final case class RefreshTokenRequest(credentials: Credentials, refreshToken: RefreshToken)
