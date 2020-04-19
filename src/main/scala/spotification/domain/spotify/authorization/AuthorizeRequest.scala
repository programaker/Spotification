package spotification.domain.spotify.authorization

import eu.timepit.refined.auto._
import spotification.domain.scope.Scope
import spotification.domain.{NonBlankString, ResponseType, UriString}

final case class AuthorizeRequest(
  client_id: ClientId,
  response_type: ResponseType, //TODO fixed value, can be removed
  redirect_uri: UriString,
  state: Option[NonBlankString],
  scope: Option[List[Scope]],
  show_dialog: Option[Boolean]
)

object AuthorizeRequest {
  // Smart constructor
  def of(
    client_id: ClientId,
    redirect_uri: UriString,
    state: Option[NonBlankString],
    scope: Option[List[Scope]]
  ): AuthorizeRequest =
    AuthorizeRequest(
      client_id = client_id,
      response_type = "code",
      redirect_uri = redirect_uri,
      state = state,
      scope = scope,
      show_dialog = None //defaults to false, which is what we want
    )
}
