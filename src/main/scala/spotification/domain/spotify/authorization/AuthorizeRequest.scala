package spotification.domain.spotify.authorization

import cats.implicits._
import eu.timepit.refined.auto._
import spotification.domain.{NonBlankString, UriString}

final case class AuthorizeRequest(
  client_id: String,
  response_type: String,
  redirect_uri: String,
  state: Option[String],
  scope: Option[String],
  show_dialog: Option[String]
)

object AuthorizeRequest {
  def of(
    credentials: Credentials,
    redirectUri: UriString,
    scope: Option[List[NonBlankString]],
    state: Option[NonBlankString]
  ): AuthorizeRequest =
    AuthorizeRequest(
      client_id = credentials.clientId,
      response_type = "code",
      redirect_uri = redirectUri,
      state = state.map(_.value),
      scope = scope.map(_.map(_.value).mkString_(" ")),
      show_dialog = None // defaults to false, which is what we want
    )
}
