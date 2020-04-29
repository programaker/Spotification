package spotification.core.spotify.authorization

import spotification.core.config.ConfigModule.SpotifyConfigService
import spotification.infra.httpclient.H4sAuthorizationService
import spotification.infra.httpclient.HttpClientModule.HttpClientService
import zio._

object AuthorizationModule {
  type AuthorizationEnv = SpotifyConfigService with AuthorizationService
  type AuthorizationIO[A] = RIO[AuthorizationEnv, A]
  type AuthorizationService = Has[AuthorizationModule.Service]

  trait Service {
    def requestToken(req: AccessTokenRequest): Task[AccessTokenResponse]
    def refreshToken(req: RefreshTokenRequest): Task[RefreshTokenResponse]
  }

  def requestToken(req: AccessTokenRequest): RIO[AuthorizationService, AccessTokenResponse] =
    ZIO.accessM(_.get.requestToken(req))

  def refreshToken(req: RefreshTokenRequest): RIO[AuthorizationService, RefreshTokenResponse] =
    ZIO.accessM(_.get.refreshToken(req))

  val layer: URLayer[HttpClientService, AuthorizationService] =
    ZLayer.fromService(new H4sAuthorizationService(_))
}
