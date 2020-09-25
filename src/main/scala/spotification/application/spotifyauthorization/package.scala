package spotification.application

import cats.implicits._
import eu.timepit.refined.auto._
import org.http4s.Uri
import spotification.domain.NonBlankStringR
import spotification.domain.spotify.authorization._
import spotification.infra.config.AuthorizationConfigModule
import spotification.infra.httpclient.makeAuthorizeUri
import spotification.infra.refineRIO
import spotification.infra.spotify.authorization.AuthorizationModule
import zio.{RIO, TaskLayer}
import zio.interop.catz._

package object spotifyauthorization {
  type SpotifyAuthorizationEnv = AuthorizationModule with AuthorizationConfigModule
  object SpotifyAuthorizationEnv {
    val live: TaskLayer[SpotifyAuthorizationEnv] =
      AuthorizationModule.live ++ AuthorizationConfigModule.live
  }

  val makeAuthorizeUriProgram: RIO[SpotifyAuthorizationEnv, Uri] =
    for {
      config <- AuthorizationConfigModule.config
      resp   <- makeAuthorizeUri(config.authorizeUri, AuthorizeRequest.make(config))
    } yield resp

  def authorizeCallbackProgram(rawCode: String): RIO[SpotifyAuthorizationEnv, AccessTokenResponse] = {
    val config = AuthorizationConfigModule.config
    val code = refineRIO[AuthorizationConfigModule, NonBlankStringR](rawCode)
    (config, code).mapN(AccessTokenRequest.make).flatMap(AuthorizationModule.requestToken)
  }

  def authorizeCallbackErrorProgram(error: String): RIO[SpotifyAuthorizationEnv, AuthorizeErrorResponse] =
    refineRIO[SpotifyAuthorizationEnv, NonBlankStringR](error).map(AuthorizeErrorResponse)

  def requestAccessTokenProgram(refreshToken: RefreshToken): RIO[SpotifyAuthorizationEnv, AccessToken] =
    for {
      config <- AuthorizationConfigModule.config

      req = RefreshTokenRequest(
        config.clientId,
        config.clientSecret,
        "refresh_token",
        refreshToken
      )

      resp <- AuthorizationModule.refreshToken(req)
    } yield resp.access_token
}
