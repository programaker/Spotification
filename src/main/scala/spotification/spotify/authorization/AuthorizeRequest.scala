package spotification.spotify.authorization

import eu.timepit.refined.auto._
import spotification.spotify.encode
import spotification.spotify.{NonBlankString, ToQueryStringParams}

final case class AuthorizeRequest(credentials: Credentials, scopes: List[Scope], state: Option[NonBlankString])

object AuthorizeRequest {
  implicit val AuthorizationRequestQsp: ToQueryStringParams[AuthorizeRequest] = req => {
    val required: Map[String, String] = Map(
      "client_id"     -> req.credentials.clientId,
      "response_type" -> "code",
      "redirect_uri"  -> encode(req.credentials.redirectUri)
    )

    val withScopes = req.scopes match {
      case Nil  => required
      case list => required + ("scope" -> list.map(_.name).mkString(encode(" ")))
    }

    req.state.fold(withScopes)(st => withScopes + ("state" -> st))
  }
}
