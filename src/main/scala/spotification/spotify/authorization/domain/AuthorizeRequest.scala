package spotification.spotify.authorization.domain

import spotification.common.domain.{NonBlankString, UriString}
import spotification.spotify.authorization.domain.scope.Scope

final case class AuthorizeRequest(
  client_id: ClientId,
  redirect_uri: UriString,
  response_type: AuthorizationResponseType,
  state: Option[NonBlankString],
  scope: Option[List[Scope]],
  show_dialog: Option[Boolean]
)
