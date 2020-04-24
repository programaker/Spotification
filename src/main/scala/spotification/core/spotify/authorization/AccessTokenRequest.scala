package spotification.core.spotify.authorization

import spotification.core._

final case class AccessTokenRequest(
  client_id: ClientId,
  client_secret: ClientSecret,
  grant_type: AccessTokenGrantType,
  code: NonBlankString,
  redirect_uri: UriString
)
