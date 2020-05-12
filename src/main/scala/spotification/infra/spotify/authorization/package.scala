package spotification.infra.spotify

import spotification.domain.config.AuthorizationConfig
import spotification.domain.spotify.authorization._
import spotification.infra.BaseEnv
import spotification.infra.concurrent.ExecutionContextModule
import spotification.infra.config.AuthorizationConfigModule
import spotification.infra.httpclient.{H4sAuthorizationService, H4sClient, HttpClientModule}
import zio._

package object authorization {
  type AuthorizationModule = Has[AuthorizationModule.Service]
  object AuthorizationModule {
    def requestToken(req: AccessTokenRequest): RIO[AuthorizationModule with BaseEnv, AccessTokenResponse] =
      ZIO.accessM(_.get.requestToken(req))

    def refreshToken(req: RefreshTokenRequest): RIO[AuthorizationModule with BaseEnv, RefreshTokenResponse] =
      ZIO.accessM(_.get.refreshToken(req))

    val layer: TaskLayer[AuthorizationModule] = {
      val l1 = ZLayer.fromServices[AuthorizationConfig, H4sClient, AuthorizationModule.Service] {
        (config, httpClient) => new H4sAuthorizationService(config.apiTokenUri, httpClient)
      }

      val l2 = BaseEnv.layer >>> AuthorizationConfigModule.layer
      val l3 = ExecutionContextModule.layer >>> HttpClientModule.layer
      (l2 ++ l3) >>> l1
    }

    trait Service {
      def requestToken(req: AccessTokenRequest): RIO[BaseEnv, AccessTokenResponse]
      def refreshToken(req: RefreshTokenRequest): RIO[BaseEnv, RefreshTokenResponse]
    }
  }
}
