package spotification.spotify.authorization

import cats.data.NonEmptyList
import cats.implicits._
import eu.timepit.refined.auto._
import spotification.spotify.{encode, NonBlankString}

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
    scopes: Option[NonEmptyList[Scope]],
    state: Option[NonBlankString]
  ): AuthorizeRequest =
    AuthorizeRequest(
      client_id = credentials.clientId,
      response_type = "code",
      redirect_uri = credentials.redirectUri,
      state = state.map(_.value),
      scope = scopes.map(_.map(_.name).mkString_(" ")),
      show_dialog = None // defaults to false, which is what we want
    )
}
