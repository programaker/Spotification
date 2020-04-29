package spotification.core.spotify.authorization

import spotification.core.CoreModule.BaseEnv
import spotification.infra.httpclient.H4sAuthorizationService
import spotification.infra.httpclient.HttpClientModule.HttpClientService
import zio.{RIO, _}

object AuthorizationModule {
  type AuthorizationService = Has[AuthorizationModule.Service]
  type AuthorizationServiceEnv = BaseEnv

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

  def requestToken(req: AccessTokenRequest): RIO[AccessorsEnv, AccessTokenResponse] =
    ZIO.accessM(_.get.requestToken(req))

  def refreshToken(req: RefreshTokenRequest): RIO[AccessorsEnv, RefreshTokenResponse] =
    ZIO.accessM(_.get.refreshToken(req))

  val layer: URLayer[HttpClientService, AuthorizationService] =
    ZLayer.fromService(new H4sAuthorizationService(_))
}
