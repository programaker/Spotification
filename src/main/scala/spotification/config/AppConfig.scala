package spotification.config

import spotification.spotify.{NonBlankString, UriString}
import spotification.spotify.authorization.Credentials

final case class AppConfig(
  credentials: Credentials,
  redirectUri: UriString,
  scopes: List[NonBlankString]
)
