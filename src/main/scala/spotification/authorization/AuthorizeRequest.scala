package spotification.authorization

import spotification.common.NonBlankString
import spotification.config.AuthorizationConfig

final case class AuthorizeRequest(
  client_id: ClientId,
  redirect_uri: RedirectUri,
  response_type: AuthorizationResponseType,
  state: Option[NonBlankString],
  scope: Option[List[Scope]],
  show_dialog: Option[Boolean]
)
object AuthorizeRequest {
  def make(cfg: AuthorizationConfig): AuthorizeRequest =
    AuthorizeRequest(
      cfg.clientId,
      cfg.redirectUri,
      AuthorizationResponseType.Code,
      state = None, // we'll not use it for now
      cfg.scopes,
      show_dialog = None // defaults to false, which is what we want
    )
}
