package spotification.domain.spotify.authorization

import spotification.domain._
import spotification.domain.config.AuthorizationConfig

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
      client_id = cfg.clientId,
      redirect_uri = cfg.redirectUri,
      response_type = AuthorizationResponseType.Code,
      state = None, //we'll not use it for now
      scope = cfg.scopes,
      show_dialog = None //defaults to false, which is what we want
    )
}
