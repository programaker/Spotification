package spotification.infra.httpclient

import org.http4s.AuthScheme.{Basic, Bearer}
import org.http4s.Credentials.Token
import spotification.core.spotify.authorization._
import zio.ZLayer
import eu.timepit.refined.auto._

private[httpclient] trait AuthorizationM {

  type H4sAuthorization = org.http4s.headers.Authorization
  val H4sAuthorization: org.http4s.headers.Authorization.type = org.http4s.headers.Authorization

  val authorizationLayer: ZLayer[H4sClient, Nothing, AuthorizationModule] =
    ZLayer.fromFunction(new H4sAuthorizationService(_))

  def authorizationBasicHeader(clientId: ClientId, clientSecret: ClientSecret): H4sAuthorization =
    H4sAuthorization(Token(Basic, base64Credentials(clientId, clientSecret)))

  def authorizationBearerHeader(accessToken: AccessToken): H4sAuthorization =
    H4sAuthorization(Token(Bearer, accessToken.value))

}
