package spotification.infra.spotify

import spotification.domain.config.AuthorizationConfig
import spotification.domain.spotify.authorization._
import spotification.infra.config.AuthorizationConfigModule
import spotification.infra.httpclient.{H4sAuthorizationService, H4sClient, HttpClientModule}
import zio._

package object authorization {
  type AuthorizationModule = Has[AuthorizationModule.Service]
  object AuthorizationModule {
    def requestToken(req: AccessTokenRequest): RIO[AuthorizationModule, AccessTokenResponse] =
      ZIO.accessM(_.get.requestToken(req))

    def refreshToken(req: RefreshTokenRequest): RIO[AuthorizationModule, RefreshTokenResponse] =
      ZIO.accessM(_.get.refreshToken(req))

    val layer: TaskLayer[AuthorizationModule] = {
      val l1 = ZLayer.fromService[AuthorizationConfig, AuthorizationModule.Service] { config =>
        new H4sAuthorizationService(config.apiTokenUri)
      }

      (AuthorizationConfigModule.layer ++ HttpClientModule.layer) >>> l1
    }

    trait Service {
      def requestToken(req: AccessTokenRequest): Task[AccessTokenResponse]
      def refreshToken(req: RefreshTokenRequest): Task[RefreshTokenResponse]
    }
  }
}
