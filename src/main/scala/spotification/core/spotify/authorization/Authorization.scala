package spotification.core.spotify.authorization

import java.nio.charset.StandardCharsets.UTF_8
import java.util.Base64

import cats.implicits._
import spotification.core.NonBlankStringR
import spotification.core.config.ConfigModule
import spotification.core.config.ConfigModule._
import zio.RIO
import spotification.application.ApplicationModule.refineRIO
import eu.timepit.refined.cats._
import spotification.core.Implicits._
import zio.interop.catz._

object Authorization {
  def authorizeCallbackProgram(rawCode: String): RIO[AuthorizationEnv, AccessTokenResponse] = {
    val config = ConfigModule.spotifyConfig
    val code = refineRIO[NonBlankStringR, SpotifyConfigService, String](rawCode)
    (config, code).mapN(AccessTokenRequest.make).flatMap(AuthorizationModule.requestToken)
  }

  def authorizeCallbackErrorProgram(error: String): RIO[AuthorizationEnv, AuthorizeErrorResponse] =
    refineRIO[NonBlankStringR, AuthorizationEnv, String](error).map(AuthorizeErrorResponse)

  def base64Credentials(clientId: ClientId, clientSecret: ClientSecret): String =
    base64(show"$clientId:$clientSecret")

  def base64(s: String): String = Base64.getEncoder.encodeToString(s.getBytes(UTF_8))
}
