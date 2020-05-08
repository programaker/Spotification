package spotification.domain.spotify.authorization

import java.nio.charset.StandardCharsets.UTF_8
import java.util.Base64

import cats.implicits._

object Authorization {
  def base64Credentials(clientId: ClientId, clientSecret: ClientSecret): String =
    base64(show"$clientId:$clientSecret")

  def base64(s: String): String =
    Base64.getEncoder.encodeToString(s.getBytes(UTF_8))
}
