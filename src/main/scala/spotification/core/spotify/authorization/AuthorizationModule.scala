package spotification.core.spotify.authorization

import spotification.core.config.ConfigModule.ConfigService
import spotification.infra.httpclient.{H4sAuthorizationService, H4sClient}
import zio.{Has, RIO, Task, URLayer, ZIO, ZLayer}

object AuthorizationModule {
  type AuthorizationEnv = ConfigService with AuthorizationService
  type AuthorizationIO[A] = RIO[AuthorizationEnv, A]
  type AuthorizationService = Has[AuthorizationModule.Service]

  trait Service {
    def authorize(req: AuthorizeRequest): Task[Unit]
    def requestToken(req: AccessTokenRequest): Task[AccessTokenResponse]
    def refreshToken(req: RefreshTokenRequest): Task[RefreshTokenResponse]
  }

  def authorize(req: AuthorizeRequest): RIO[AuthorizationService, Unit] =
    ZIO.accessM(_.get.authorize(req))

  def requestToken(req: AccessTokenRequest): RIO[AuthorizationService, AccessTokenResponse] =
    ZIO.accessM(_.get.requestToken(req))

  def refreshToken(req: RefreshTokenRequest): RIO[AuthorizationService, RefreshTokenResponse] =
    ZIO.accessM(_.get.refreshToken(req))

  val layer: URLayer[H4sClient, AuthorizationService] =
    ZLayer.fromFunction(new H4sAuthorizationService(_))
}
