package spotification.domain.spotify.authorization

import spotification.domain.{NonBlankString, UriString}
import eu.timepit.refined.auto._

final case class AccessTokenRequest(
  credentials: Credentials,
  grant_type: String,
  code: String,
  redirect_uri: String
)

object AccessTokenRequest {
  def of(credentials: Credentials, redirectUri: UriString, code: NonBlankString): AccessTokenRequest =
    AccessTokenRequest(
      credentials = credentials,
      grant_type = "authorization_code",
      code = code,
      redirect_uri = redirectUri
    )
}
