package spotification.domain.config

import spotification.domain.UriString
import spotification.domain.spotify.authorization.{ClientId, ClientSecret, Scope}

final case class SpotifyConfig(
  clientId: ClientId,
  clientSecret: ClientSecret,
  redirectUri: UriString,
  scopes: Option[List[Scope]]
)
