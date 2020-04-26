package spotification.core.spotify.authorization

import spotification.core.config.ConfigModule.SpotifyConfigService
import zio.{Has, RIO, Task, ZIO}

object AuthorizationModule {
  type AuthorizationEnv = SpotifyConfigService with AuthorizationService
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
}