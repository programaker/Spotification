package spotification.core.spotify.authorization

import java.nio.charset.StandardCharsets.UTF_8
import java.util.Base64

import cats.implicits._
import eu.timepit.refined.cats._
import spotification.core.Core._
import spotification.core.Implicits._
import spotification.core.NonBlankStringR
import spotification.core.config.ConfigModule
import spotification.core.config.ConfigModule._
import spotification.core.spotify.authorization.AuthorizationModule.{AuthorizationEnv, AuthorizationIO}
import zio.interop.catz._

object Authorization {
  val authorizeProgram: AuthorizationIO[Unit] =
    for {
      config <- ConfigModule.spotifyConfig
      _      <- AuthorizationModule.authorize(AuthorizeRequest.make(config))
    } yield ()

  def authorizeCallbackProgram(rawCode: String): AuthorizationIO[AccessTokenResponse] = {
    val config = ConfigModule.spotifyConfig
    val code = refineRIO[NonBlankStringR, SpotifyConfigService, String](rawCode)
    (config, code).mapN(AccessTokenRequest.make).flatMap(AuthorizationModule.requestToken)
  }

  def authorizeCallbackErrorProgram(error: String): AuthorizationIO[AuthorizeErrorResponse] =
    refineRIO[NonBlankStringR, AuthorizationEnv, String](error).map(AuthorizeErrorResponse)

  def base64Credentials(clientId: ClientId, clientSecret: ClientSecret): String =
    base64(show"$clientId:$clientSecret")

  def base64(s: String): String = Base64.getEncoder.encodeToString(s.getBytes(UTF_8))
}
