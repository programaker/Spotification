package spotification.domain.spotify.authorization

import spotification.domain.{NonBlankString, UriString}

final case class AccessTokenRequest(
  client_id: ClientId,
  client_secret: ClientSecret,
  code: NonBlankString,
  redirect_uri: UriString
)
