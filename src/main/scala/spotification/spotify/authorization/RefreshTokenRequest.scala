package spotification.spotify.authorization

import eu.timepit.refined.auto._

final case class RefreshTokenRequest(
  credentials: Credentials,
  grant_type: String,
  refresh_token: String
)

object RefreshTokenRequest {
  def of(credentials: Credentials, refreshToken: RefreshToken): RefreshTokenRequest =
    RefreshTokenRequest(
      credentials = credentials,
      grant_type = "refresh_token",
      refresh_token = refreshToken.value
    )
}
