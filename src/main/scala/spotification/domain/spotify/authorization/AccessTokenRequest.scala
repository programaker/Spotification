package spotification.domain.spotify.authorization

import spotification.domain.{AccessTokenGrantType, NonBlankString, UriString}

final case class AccessTokenRequest(
  client_id: ClientId,
  client_secret: ClientSecret,
  grant_type: AccessTokenGrantType,
  code: NonBlankString,
  redirect_uri: UriString
)
