package spotification.domain.spotify.authorization

import spotification.domain.{GrantType, NonBlankString, UriString}
import eu.timepit.refined.auto._

final case class AccessTokenRequest(
  client_id: ClientId,
  client_secret: ClientSecret,
  grant_type: GrantType, //TODO fixed value, can be removed
  code: NonBlankString,
  redirect_uri: UriString
)

object AccessTokenRequest {
  def of(
    client_id: ClientId,
    client_secret: ClientSecret,
    code: NonBlankString,
    redirect_uri: UriString
  ): AccessTokenRequest =
    AccessTokenRequest(
      client_id = client_id,
      client_secret = client_secret,
      grant_type = "authorization_code",
      code = code,
      redirect_uri = redirect_uri
    )
}
