package spotification.application

import cats.implicits._
import spotification.application.ApplicationModule.refineRIO
import spotification.core.NonBlankStringR
import ConfigModule.SpotifyConfigService
import spotification.core.spotify.authorization._
import spotification.infra.httpclient.H4sAuthorizationService
import spotification.infra.httpclient.HttpClientModule.HttpClientService
import zio._
import zio.interop.catz._

object AuthorizationModule {
  def authorizeCallbackProgram(rawCode: String): RIO[AuthorizationEnv, AccessTokenResponse] = {
    val config = ConfigModule.spotifyConfig
    val code = refineRIO[NonBlankStringR, SpotifyConfigService, String](rawCode)
    (config, code).mapN(AccessTokenRequest.make).flatMap(AuthorizationModule.requestToken)
  }

  def authorizeCallbackErrorProgram(error: String): RIO[AuthorizationEnv, AuthorizeErrorResponse] =
    refineRIO[NonBlankStringR, AuthorizationEnv, String](error).map(AuthorizeErrorResponse)

  def layer: URLayer[HttpClientService, AuthorizationService] =
    ZLayer.fromService(new H4sAuthorizationService(_))

  def requestToken(req: AccessTokenRequest): RIO[AccessorsEnv, AccessTokenResponse] =
    ZIO.accessM(_.get.requestToken(req))

  def refreshToken(req: RefreshTokenRequest): RIO[AccessorsEnv, RefreshTokenResponse] =
    ZIO.accessM(_.get.refreshToken(req))

  type AuthorizationEnv = AuthorizationService with SpotifyConfigService with BaseEnv
  type AuthorizationServiceEnv = BaseEnv //abstracting BaseEnv in case Auth. needs to diverge
  type AuthorizationService = Has[AuthorizationModule.Service]

  // Service functions require an Env but the accessors require the Service itself
  //
  // We want `_.get` to get the Service but it will only happen if the Service
  // is the first one in the composition. Order matters if you want type inference!
  //
  // An alternative is parameterize get like this `_.get[Service]`, but its more verbose
  type AccessorsEnv = AuthorizationService with AuthorizationServiceEnv

  trait Service {
    def requestToken(req: AccessTokenRequest): RIO[AuthorizationServiceEnv, AccessTokenResponse]
    def refreshToken(req: RefreshTokenRequest): RIO[AuthorizationServiceEnv, RefreshTokenResponse]
  }
}
