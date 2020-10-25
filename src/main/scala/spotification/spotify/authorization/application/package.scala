package spotification.spotify.authorization

import spotification.config.AuthorizationConfig
import spotification.common.infra.httpclient._
import spotification.spotify.authorization.infra.H4sAuthorizationService
import zio._

package object application {
  type AuthorizationModule = Has[AuthorizationService]
  object AuthorizationModule {
    val live: TaskLayer[AuthorizationModule] = {
      val l1 = ZLayer.fromServices[AuthorizationConfig, H4sClient, AuthorizationService] { (config, httpClient) =>
        new H4sAuthorizationService(config.apiTokenUri, httpClient)
      }

      (AuthorizationConfigModule.live ++ HttpClientModule.live) >>> l1
    }
  }

  def requestToken(req: AccessTokenRequest): RIO[AuthorizationModule, AccessTokenResponse] =
    ZIO.accessM(_.get.requestToken(req))

  def refreshToken(req: RefreshTokenRequest): RIO[AuthorizationModule, RefreshTokenResponse] =
    ZIO.accessM(_.get.refreshToken(req))
}
