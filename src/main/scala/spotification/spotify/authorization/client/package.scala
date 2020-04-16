package spotification.spotify.authorization

import org.http4s.{AuthScheme, Credentials => H4sCredentials}
import org.http4s.headers.{Authorization => H4sAuthorization}
import org.http4s.client.Client
import eu.timepit.refined.auto._
import zio.Task
import zio.ZLayer
import spotification.spotify.authorization.module._

import H4sCredentials.Token
import AuthScheme._

package object client {

  def authorizationBasicHeader(credentials: Credentials): H4sAuthorization =
    H4sAuthorization(Token(Basic, base64Credentials(credentials)))

  def authorizationBearerHeader(accessToken: AccessToken): H4sAuthorization =
    H4sAuthorization(Token(Bearer, accessToken.value))

  type HttpClient = Client[Task]

  val authorizationLayer: ZLayer[HttpClient, Nothing, Authorization] =
    ZLayer.fromFunction(new H4sAuthorizationService(_))

}
