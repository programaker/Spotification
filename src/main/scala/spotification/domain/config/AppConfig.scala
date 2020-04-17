package spotification.domain.config

import spotification.domain.spotify.authorization.Credentials
import spotification.domain.{NonBlankString, UriString}

final case class AppConfig(
  credentials: Credentials,
  redirectUri: UriString,
  scopes: Option[List[NonBlankString]]
)
