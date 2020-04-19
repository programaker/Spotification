package spotification.domain.config

import spotification.domain.UriString
import spotification.domain.scope.Scope
import spotification.domain.spotify.authorization.{ClientId, ClientSecret}

final case class AppConfig(
  clientId: ClientId,
  clientSecret: ClientSecret,
  redirectUri: UriString,
  scopes: Option[List[Scope]]
)
