package spotification.domain.spotify.authorization

import spotification.domain.scope.Scope
import spotification.domain.{NonBlankString, UriString}

final case class AuthorizeRequest(
  client_id: ClientId,
  redirect_uri: UriString,
  state: Option[NonBlankString],
  scope: Option[List[Scope]],
  show_dialog: Option[Boolean]
)
