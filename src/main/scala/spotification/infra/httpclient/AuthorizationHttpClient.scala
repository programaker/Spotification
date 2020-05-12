package spotification.infra.httpclient

import cats.implicits._
import eu.timepit.refined.auto._
import eu.timepit.refined.cats._
import org.http4s.AuthScheme.{Basic, Bearer}
import org.http4s.Credentials.Token
import org.http4s.Uri
import spotification.domain.spotify.authorization.Authorization.base64Credentials
import spotification.domain.spotify.authorization._
import spotification.infra.httpclient.HttpClient.{addScopeParam, makeQueryString, _}
import zio.Task

object AuthorizationHttpClient {
  def authorizationBasicHeader(clientId: ClientId, clientSecret: ClientSecret): H4sAuthorization =
    H4sAuthorization(Token(Basic, base64Credentials(clientId, clientSecret)))

  def authorizationBearerHeader(accessToken: AccessToken): H4sAuthorization =
    H4sAuthorization(Token(Bearer, accessToken.value))

  def makeAuthorizeUri(authorizeUri: AuthorizeUri, req: AuthorizeRequest): Task[Uri] = {
    // required params
    val params: ParamMap = Map(
      "client_id"     -> req.client_id.show,
      "response_type" -> req.response_type,
      "redirect_uri"  -> encode(req.redirect_uri.show)
    )

    // optional params
    val params2 = req.show_dialog
      .fold(params)(b => params + ("show_dialog" -> b.show))
    val params3 = req.state
      .fold(params2)(s => params2 + ("state" -> s.show))

    // decoding optional scope
    val params4 = req.scope
      .map(addScopeParam(params3, _))
      .map(_.leftMap(new Exception(_)))
      .map(Task.fromEither(_))
      .getOrElse(Task(params))

    // https://github.com/http4s/http4s/issues/2445
    // I did this for the same reason I've created `HttpClient.encode` function
    // which is the encoding choice of http4s not being accepted by Spotify.
    // Adding everything to `path` bypasses their encoding.
    params4
      .map(makeQueryString)
      .map(q => Uri(path = show"$authorizeUri?$q"))
  }
}
