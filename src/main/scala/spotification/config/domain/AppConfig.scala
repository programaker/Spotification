package spotification.config.domain

import spotification.common.domain.UriString
import spotification.spotify.authorization.domain.scope.Scope
import spotification.spotify.authorization.domain.{ClientId, ClientSecret}

final case class AppConfig(
  clientId: ClientId,
  clientSecret: ClientSecret,
  redirectUri: UriString,
  scopes: Option[List[Scope]]
)
