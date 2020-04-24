package spotification.core.spotify.authorization

import spotification.core._

final case class AuthorizeRequest(
  client_id: ClientId,
  redirect_uri: UriString,
  response_type: AuthorizationResponseType,
  state: Option[NonBlankString],
  scope: Option[List[Scope]],
  show_dialog: Option[Boolean]
)
