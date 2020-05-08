package spotification.infra.spotify

import spotification.domain.spotify.authorization._
import spotification.infra.BaseEnv
import spotification.infra.httpclient.{H4sAuthorizationService, HttpClientModule}
import zio.{Has, RIO, URLayer, ZIO, ZLayer}

package object authorization {
  // Abstracts Authorization services dependencies, in case they change
  type AuthorizationServiceEnv = BaseEnv

  type AuthorizationModule = Has[AuthorizationModule.Service]
  object AuthorizationModule {
    // Service functions require an Env but the accessors require the Service itself
    //
    // We want `_.get` to get the Service but it will only happen if the Service
    // is the first one in the composition. Order matters if you want type inference!
    //
    // An alternative is parameterize get like this `_.get[Service]`, but its more verbose
    type AccessorsEnv = AuthorizationModule with AuthorizationServiceEnv

    val layer: URLayer[HttpClientModule, AuthorizationModule] =
      ZLayer.fromService(new H4sAuthorizationService(_))

    def requestToken(req: AccessTokenRequest): RIO[AccessorsEnv, AccessTokenResponse] =
      ZIO.accessM(_.get.requestToken(req))

    def refreshToken(req: RefreshTokenRequest): RIO[AccessorsEnv, RefreshTokenResponse] =
      ZIO.accessM(_.get.refreshToken(req))

    trait Service {
      def requestToken(req: AccessTokenRequest): RIO[AuthorizationServiceEnv, AccessTokenResponse]
      def refreshToken(req: RefreshTokenRequest): RIO[AuthorizationServiceEnv, RefreshTokenResponse]
    }
  }
}
