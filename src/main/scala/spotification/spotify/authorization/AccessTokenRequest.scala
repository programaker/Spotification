package spotification.spotify.authorization

import spotification.spotify.NonBlankString
import eu.timepit.refined.auto._

final case class AccessTokenRequest(
  credentials: Credentials,
  grant_type: String,
  code: String,
  redirect_uri: String
)

object AccessTokenRequest {
  def of(credentials: Credentials, code: NonBlankString): AccessTokenRequest =
    AccessTokenRequest(
      credentials = credentials,
      grant_type = "authorization_code",
      code = code,
      redirect_uri = credentials.redirectUri
    )
}
