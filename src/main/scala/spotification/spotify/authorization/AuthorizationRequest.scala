package spotification.spotify.authorization

import eu.timepit.refined.auto._
import spotification.spotify.encode
import spotification.spotify.{NonBlankString, ToQueryStringParams}

final case class AuthorizationRequest(credentials: Credentials, scopes: List[Scope], state: Option[NonBlankString])

object AuthorizationRequest {
  implicit val AuthorizationRequestQsp: ToQueryStringParams[AuthorizationRequest] = req => {
    val required: Map[String, String] = Map(
      "client_id"     -> req.credentials.clientId,
      "response_type" -> AuthorizeResponseType.Code.name,
      "redirect_uri"  -> encode(req.credentials.redirectUri)
    )

    val withScopes = req.scopes match {
      case Nil  => required
      case list => required + ("scope" -> list.map(_.name).mkString(" "))
    }

    req.state.fold(withScopes)(st => withScopes + ("state" -> st))
  }
}
