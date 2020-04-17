package spotification.domain.spotify.authorization

import cats.data.NonEmptyList
import cats.implicits._
import spotification.domain.{NonBlankString, UriString}
import eu.timepit.refined.auto._

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
    scope: Option[NonEmptyList[Scope]],
    state: Option[NonBlankString]
  ): AuthorizeRequest =
    AuthorizeRequest(
      client_id = credentials.clientId,
      response_type = "code",
      redirect_uri = redirectUri,
      state = state.map(_.value),
      scope = scope.map(_.map(_.name).mkString_(" ")),
      show_dialog = None // defaults to false, which is what we want
    )
}
