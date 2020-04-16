package spotification.spotify

import java.nio.charset.StandardCharsets.UTF_8
import java.util.Base64

import zio.{Has, RIO, Task, ZIO}

package object authorization {

  def authorizationBasicHeader(credentials: Credentials): String =
    s"Authorization: Basic ${base64Credentials(credentials)}"

  def authorizationBearerHeader(accessToken: AccessToken): String =
    s"Authorization: Bearer ${accessToken.value}"

  def base64Credentials(credentials: Credentials): String =
    Base64.getEncoder.encodeToString(s"${credentials.clientId}:${credentials.clientSecret}".getBytes(UTF_8))

}
