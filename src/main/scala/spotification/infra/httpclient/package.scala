package spotification.infra

import org.http4s.AuthScheme.{Basic, Bearer}
import org.http4s.Credentials.Token
import org.http4s.client.Client
import org.http4s.headers
import spotification.domain.spotify.authorization.{base64Credentials, AccessToken, Authorization, Credentials}
import zio.{Task, ZLayer}
import eu.timepit.refined.auto._

package object httpclient {

  type H4sClient = Client[Task]

  type H4sAuthorization = org.http4s.headers.Authorization
  val H4sAuthorization: org.http4s.headers.Authorization.type = org.http4s.headers.Authorization

  def authorizationBasicHeader(credentials: Credentials): H4sAuthorization =
    H4sAuthorization(Token(Basic, base64Credentials(credentials)))

  def authorizationBearerHeader(accessToken: AccessToken): headers.Authorization =
    H4sAuthorization(Token(Bearer, accessToken.value))

  val authorizationLayer: ZLayer[H4sClient, Nothing, Authorization] =
    ZLayer.fromFunction(new H4sAuthorizationService(_))

}
