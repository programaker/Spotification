package spotification.authorization

import cats.implicits._
import eu.timepit.refined.auto._
import eu.timepit.refined.cats._
import org.http4s.AuthScheme.{Basic, Bearer}
import org.http4s.Credentials.Token
import org.http4s.Uri
import spotification.authorization.service.{AuthorizationService, AuthorizationServiceEnv}
import spotification.common.httpclient.{H4sClient, HttpClientLayer}
import spotification.config.AuthorizationConfig
import spotification.config.source.AuthorizationConfigLayer
import spotification.effect.leftStringEitherToTask
import zio.{Task, TaskLayer, ZLayer}

package object httpclient {
  type H4sAuthorization = org.http4s.headers.Authorization
  val H4sAuthorization: org.http4s.headers.Authorization.type = org.http4s.headers.Authorization

  val AuthorizationServiceLayer: TaskLayer[AuthorizationServiceEnv] = {
    val l1 = ZLayer.fromServices[AuthorizationConfig, H4sClient, AuthorizationService] { (config, httpClient) =>
      new H4sAuthorizationService(config.apiTokenUri, httpClient)
    }

    (AuthorizationConfigLayer ++ HttpClientLayer) >>> l1
  }

  def authorizationBasicHeader(clientId: ClientId, clientSecret: ClientSecret): H4sAuthorization =
    H4sAuthorization(Token(Basic, base64Credentials(clientId, clientSecret)))

  def authorizationBearerHeader(accessToken: AccessToken): H4sAuthorization =
    H4sAuthorization(Token(Bearer, accessToken.value))

  def makeAuthorizeH4sUri(authorizeUri: AuthorizeUri, req: AuthorizeRequest): Task[Uri] =
    leftStringEitherToTask(makeAuthorizeUri(authorizeUri, req))
      .map(uriString => Uri(path = show"$uriString"))
}
