package spotification.spotify

import java.nio.charset.StandardCharsets.UTF_8
import java.util.Base64

package object authorization {

  def base64Credentials(credentials: Credentials): String =
    Base64.getEncoder.encodeToString(s"${credentials.clientId}:${credentials.clientSecret}".getBytes(UTF_8))

}
