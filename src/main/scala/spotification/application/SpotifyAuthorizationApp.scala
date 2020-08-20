package spotification.application

import cats.implicits._
import org.http4s.Uri
import spotification.domain.NonBlankStringR
import spotification.domain.spotify.authorization._
import spotification.infra._
import spotification.infra.config.AuthorizationConfigModule
import spotification.infra.httpclient.makeAuthorizeUri
import spotification.infra.spotify.authorization.AuthorizationModule
import zio.RIO
import zio.interop.catz._

object SpotifyAuthorizationApp {
  val makeAuthorizeUriProgram: RIO[SpotifyAuthorizationAppEnv, Uri] =
    for {
      config <- AuthorizationConfigModule.config
      resp   <- makeAuthorizeUri(config.authorizeUri, AuthorizeRequest.make(config))
    } yield resp

  def authorizeCallbackProgram(rawCode: String): RIO[SpotifyAuthorizationAppEnv, AccessTokenResponse] = {
    val config = AuthorizationConfigModule.config
    val code = refineRIO[AuthorizationConfigModule, NonBlankStringR](rawCode)
    (config, code).mapN(AccessTokenRequest.make).flatMap(AuthorizationModule.requestToken)
  }

  def authorizeCallbackErrorProgram(error: String): RIO[SpotifyAuthorizationAppEnv, AuthorizeErrorResponse] =
    refineRIO[SpotifyAuthorizationAppEnv, NonBlankStringR](error).map(AuthorizeErrorResponse)

  def requestAccessTokenProgram(refreshToken: RefreshToken): RIO[SpotifyAuthorizationAppEnv, AccessToken] =
    for {
      config <- AuthorizationConfigModule.config

      req = RefreshTokenRequest(
        config.clientId,
        config.clientSecret,
        RefreshTokenGrantType.RefreshToken,
        refreshToken
      )

      resp <- AuthorizationModule.refreshToken(req)
    } yield resp.access_token
}
