package spotification.application

import cats.implicits._
import spotification.domain.NonBlankStringR
import spotification.domain.spotify.authorization.{AccessTokenRequest, AccessTokenResponse, AuthorizeErrorResponse}
import spotification.infra.Infra.refineRIO
import spotification.infra.config.SpotifyConfigModule
import spotification.infra.spotify.authorization.{AuthorizationEnv, AuthorizationModule}
import zio.RIO
import zio.interop.catz._

object SpotifyAuthorizationApp {
  def authorizeCallbackProgram(rawCode: String): RIO[AuthorizationEnv, AccessTokenResponse] = {
    val config = SpotifyConfigModule.config
    val code = refineRIO[NonBlankStringR, SpotifyConfigModule, String](rawCode)
    (config, code).mapN(AccessTokenRequest.make).flatMap(AuthorizationModule.requestToken)
  }

  def authorizeCallbackErrorProgram(error: String): RIO[AuthorizationEnv, AuthorizeErrorResponse] =
    refineRIO[NonBlankStringR, AuthorizationEnv, String](error).map(AuthorizeErrorResponse)
}
