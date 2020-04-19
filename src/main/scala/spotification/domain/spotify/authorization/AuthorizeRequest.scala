package spotification.domain.spotify.authorization

import spotification.domain.scope.Scope
import spotification.domain.{AuthorizationResponseType, NonBlankString, UriString}

final case class AuthorizeRequest(
  client_id: ClientId,
  redirect_uri: UriString,
  response_type: AuthorizationResponseType,
  state: Option[NonBlankString],
  scope: Option[List[Scope]],
  show_dialog: Option[Boolean]
)
