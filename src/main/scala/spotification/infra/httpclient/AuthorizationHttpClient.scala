package spotification.infra.httpclient

import cats.implicits._
import eu.timepit.refined.auto._
import org.http4s.AuthScheme.{Basic, Bearer}
import org.http4s.Credentials.Token
import org.http4s.Uri
import org.http4s.implicits._
import spotification.core.config.ConfigModule
import spotification.core.spotify.authorization.Authorization.base64Credentials
import spotification.core.spotify.authorization.AuthorizationModule.AuthorizationIO
import spotification.core.spotify.authorization.{AccessToken, AuthorizeRequest, ClientId, ClientSecret}
import spotification.infra.httpclient.HttpClient.{addScopeParam, toParams}
import zio.Task

object AuthorizationHttpClient {
  val accountsUri: Uri = uri"https://accounts.spotify.com"
  val authorizeUri: Uri = accountsUri.withPath("/authorize")
  val apiTokenUri: Uri = accountsUri.withPath("/api/token")

  val authorizeUriProgram: AuthorizationIO[Uri] = {
    for {
      config <- ConfigModule.spotifyConfig
      resp   <- makeAuthorizeUri(AuthorizeRequest.make(config))
    } yield resp
  }

  def authorizationBasicHeader(clientId: ClientId, clientSecret: ClientSecret): H4sAuthorization =
    H4sAuthorization(Token(Basic, base64Credentials(clientId, clientSecret)))

  def authorizationBearerHeader(accessToken: AccessToken): H4sAuthorization =
    H4sAuthorization(Token(Bearer, accessToken.value))

  def makeAuthorizeUri(req: AuthorizeRequest): Task[Uri] = {
    val params = toParams(req)

    // `scope` can't be generically built due to the required
    // List[Scope] => SpaceSeparatedString transformation
    val params2 = req.scope
      .map(addScopeParam(params, _))
      .map(_.leftMap(new Exception(_)))
      .map(Task.fromEither(_))
      .getOrElse(Task(params))

    params2.map(authorizeUri.withQueryParams(_))
  }
}
