package spotification.infra.spotify

import spotification.domain.config.AuthorizationConfig
import spotification.domain.spotify.authorization._
import spotification.infra.config.AuthorizationConfigModule
import spotification.infra.httpclient._
import zio._

package object authorization {
  type AuthorizationModule = Has[AuthorizationService]
  object AuthorizationModule {
    def requestToken(req: AccessTokenRequest): RIO[AuthorizationModule, AccessTokenResponse] =
      ZIO.accessM(_.get.requestToken(req))

    def refreshToken(req: RefreshTokenRequest): RIO[AuthorizationModule, RefreshTokenResponse] =
      ZIO.accessM(_.get.refreshToken(req))

    val live: TaskLayer[AuthorizationModule] = {
      val l1 = ZLayer.fromServices[AuthorizationConfig, H4sClient, AuthorizationService] { (config, httpClient) =>
        new H4sAuthorizationService(config.apiTokenUri, httpClient)
      }

      (AuthorizationConfigModule.live ++ HttpClientModule.live) >>> l1
    }
  }
}
