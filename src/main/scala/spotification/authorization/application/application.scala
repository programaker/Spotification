package spotification.authorization

import cats.implicits._
import eu.timepit.refined.auto._
import org.http4s.Uri
import spotification.common.{NonBlankStringR, UriString}
import spotification.common.application.{leftStringEitherToRIO, refineRIO}
import spotification.config.application.{AuthorizationConfigEnv, authorizationConfig}
import zio.{Has, RIO, ZIO}
import zio.interop.catz._

package object application {
  type AuthorizationServiceEnv = Has[AuthorizationService]
  type SpotifyAuthorizationEnv = AuthorizationServiceEnv with AuthorizationConfigEnv

  def requestToken(req: AccessTokenRequest): RIO[AuthorizationServiceEnv, AccessTokenResponse] =
    ZIO.accessM(_.get.requestToken(req))

  def refreshToken(req: RefreshTokenRequest): RIO[AuthorizationServiceEnv, RefreshTokenResponse] =
    ZIO.accessM(_.get.refreshToken(req))

  def makeAuthorizeUriProgram: RIO[SpotifyAuthorizationEnv, UriString] =
    authorizationConfig
      .map(config => makeAuthorizeUri(config.authorizeUri, AuthorizeRequest.make(config)))
      .flatMap(leftStringEitherToRIO)

  def authorizeCallbackProgram(rawCode: String): RIO[SpotifyAuthorizationEnv, AccessTokenResponse] =
    for {
      config <- authorizationConfig
      code   <- refineRIO[AuthorizationConfigEnv, NonBlankStringR](rawCode)
      resp   <- requestToken(AccessTokenRequest.make(config, code))
    } yield resp

  def authorizeCallbackErrorProgram(error: String): RIO[SpotifyAuthorizationEnv, AuthorizeErrorResponse] =
    refineRIO[SpotifyAuthorizationEnv, NonBlankStringR](error).map(AuthorizeErrorResponse)

  def requestAccessTokenProgram(token: RefreshToken): RIO[SpotifyAuthorizationEnv, AccessToken] =
    for {
      config <- authorizationConfig

      req = RefreshTokenRequest(
        config.clientId,
        config.clientSecret,
        RefreshTokenGrantType.RefreshToken,
        token
      )

      resp <- refreshToken(req)
    } yield resp.access_token
}
