package spotification.spotify.authorization

import eu.timepit.refined.auto._
import org.http4s.AuthScheme._
import org.http4s.Credentials.Token
import org.http4s.client.Client
import org.http4s.headers.{Authorization => H4sAuthorization}
import zio.Task

package object client {

  def authorizationBasicHeader(credentials: Credentials): H4sAuthorization =
    H4sAuthorization(Token(Basic, base64Credentials(credentials)))

  def authorizationBearerHeader(accessToken: AccessToken): H4sAuthorization =
    H4sAuthorization(Token(Bearer, accessToken.value))

  type HttpClient = Client[Task]

}
