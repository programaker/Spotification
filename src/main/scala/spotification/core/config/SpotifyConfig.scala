package spotification.core.config

import spotification.core.UriString
import spotification.core.spotify.authorization.{ClientId, ClientSecret, Scope}

final case class SpotifyConfig(
  clientId: ClientId,
  clientSecret: ClientSecret,
  redirectUri: UriString,
  scopes: Option[List[Scope]]
)
