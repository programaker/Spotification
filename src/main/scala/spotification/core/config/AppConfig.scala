package spotification.core.config

import spotification.core.UriString
import spotification.core.spotify.authorization._

final case class AppConfig(
  clientId: ClientId,
  clientSecret: ClientSecret,
  redirectUri: UriString,
  scopes: Option[List[Scope]]
)
