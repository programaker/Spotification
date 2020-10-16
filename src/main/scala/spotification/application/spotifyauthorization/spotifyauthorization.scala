package spotification.application

import cats.implicits._
import eu.timepit.refined.auto._
import org.http4s.Uri
import spotification.domain.NonBlankStringR
import spotification.domain.spotify.authorization._
import spotification.infra.config.{AuthorizationConfigModule, authorizationConfig}
import spotification.infra.httpclient.makeAuthorizeUri
import spotification.infra.refineRIO
import spotification.infra.spotify.authorization.{AuthorizationModule, refreshToken, requestToken}
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
      config <- authorizationConfig
      resp   <- makeAuthorizeUri(config.authorizeUri, AuthorizeRequest.make(config))
    } yield resp

  def authorizeCallbackProgram(rawCode: String): RIO[SpotifyAuthorizationEnv, AccessTokenResponse] =
    (authorizationConfig, refineRIO[AuthorizationConfigModule, NonBlankStringR](rawCode))
      .mapN(AccessTokenRequest.make)
      .flatMap(requestToken)

  def authorizeCallbackErrorProgram(error: String): RIO[SpotifyAuthorizationEnv, AuthorizeErrorResponse] =
    refineRIO[SpotifyAuthorizationEnv, NonBlankStringR](error).map(AuthorizeErrorResponse)

  def requestAccessTokenProgram(token: RefreshToken): RIO[SpotifyAuthorizationEnv, AccessToken] =
    for {
      config <- authorizationConfig

      req = RefreshTokenRequest(
        config.clientId,
        config.clientSecret,
        RefreshTokenGrantType.refreshToken,
        token
      )

      resp <- refreshToken(req)
    } yield resp.access_token
}
