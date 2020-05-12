package spotification.domain.config

import spotification.domain.spotify.authorization.{ClientId, ClientSecret, Scope}
import spotification.domain.{NonBlankString, UriString}

final case class AuthorizationConfig(
  clientId: ClientId,
  clientSecret: ClientSecret,
  redirectUri: UriString,
  scopes: Option[List[Scope]],
  refreshToken: Option[NonBlankString]
)
