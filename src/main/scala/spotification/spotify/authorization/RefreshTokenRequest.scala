package spotification.spotify.authorization

final case class RefreshTokenRequest(credentials: Credentials, refreshToken: RefreshToken)
