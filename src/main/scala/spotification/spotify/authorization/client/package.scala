package spotification.spotify.authorization

import org.http4s.{AuthScheme, Credentials => H4sCredentials}
import org.http4s.headers.{Authorization => H4sAuthorization}
import eu.timepit.refined.auto._

package object client {

  def authorizationBasicHeader(credentials: Credentials): H4sAuthorization =
    H4sAuthorization(H4sCredentials.Token(AuthScheme.Basic, base64Credentials(credentials)))

  def authorizationBearerHeader(accessToken: AccessToken): H4sAuthorization =
    H4sAuthorization(H4sCredentials.Token(AuthScheme.Bearer, accessToken.value))

}
