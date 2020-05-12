package spotification.domain.config

import spotification.domain.NonBlankString
import spotification.domain.spotify.authorization._

final case class AuthorizationConfig(
  clientId: ClientId,
  clientSecret: ClientSecret,
  redirectUri: RedirectUri,
  authorizeUri: AuthorizeUri,
  apiTokenUri: ApiTokenUri,
  scopes: Option[List[Scope]],
  refreshToken: Option[NonBlankString]
)
