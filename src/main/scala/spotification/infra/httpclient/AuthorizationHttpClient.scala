package spotification.infra.httpclient

import cats.implicits._
import eu.timepit.refined.auto._
import org.http4s.AuthScheme.{Basic, Bearer}
import org.http4s.Credentials.Token
import org.http4s.Uri
import spotification.core.config.ConfigModule
import spotification.core.spotify.authorization.Authorization.base64Credentials
import spotification.core.spotify.authorization._
import spotification.infra.httpclient.HttpClient.{addScopeParam, makeQueryString, toParams}
import zio.{RIO, Task}

object AuthorizationHttpClient {
  val accountsUri: String = "https://accounts.spotify.com"
  val authorizeUri: String = s"$accountsUri/authorize"
  val apiTokenUri: String = s"$accountsUri/api/token"

  val makeAuthorizeUriProgram: RIO[AuthorizationEnv, Uri] = {
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

    // https://github.com/http4s/http4s/issues/2445
    // I did this for the same reason I've created `HttpClient.encode` function
    // which the encoding choice of http4s
    params2
      .map(makeQueryString)
      .map(q => Uri(path = s"$authorizeUri?$q"))
  }
}
