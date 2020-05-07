package spotification.domain.config

import spotification.domain.{NonBlankString, UriString}
import spotification.domain.spotify.authorization.{ClientId, ClientSecret, Scope}

final case class SpotifyConfig(
  clientId: ClientId,
  clientSecret: ClientSecret,
  redirectUri: UriString,
  scopes: Option[List[Scope]],
  refreshToken: Option[NonBlankString]
)
