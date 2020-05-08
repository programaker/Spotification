package spotification.application

import cats.implicits._
import spotification.domain.NonBlankStringR
import spotification.domain.spotify.authorization.{AccessTokenRequest, AccessTokenResponse, AuthorizeErrorResponse}
import spotification.infra.Infra.refineRIO
import spotification.infra.config.ConfigZIO
import spotification.infra.config.ConfigZIO.SpotifyConfigService
import spotification.infra.spotify.authorization.AuthorizationZIO
import spotification.infra.spotify.authorization.AuthorizationZIO.AuthorizationEnv
import zio.RIO
import zio.interop.catz._

object SpotifyAuthorizationApp {
  def authorizeCallbackProgram(rawCode: String): RIO[AuthorizationEnv, AccessTokenResponse] = {
    val config = ConfigZIO.spotifyConfig
    val code = refineRIO[NonBlankStringR, SpotifyConfigService, String](rawCode)
    (config, code).mapN(AccessTokenRequest.make).flatMap(AuthorizationZIO.requestToken)
  }

  def authorizeCallbackErrorProgram(error: String): RIO[AuthorizationEnv, AuthorizeErrorResponse] =
    refineRIO[NonBlankStringR, AuthorizationEnv, String](error).map(AuthorizeErrorResponse)
}
