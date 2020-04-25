package spotification.core.spotify.authorization

import zio.{Has, RIO, Task, ZIO}

private[authorization] trait AuthorizationM {

  type AuthorizationModule = Has[AuthorizationModule.Service]
  object AuthorizationModule {
    trait Service {
      def authorize(req: AuthorizeRequest): Task[Unit]
      def requestToken(req: AccessTokenRequest): Task[AccessTokenResponse]
      def refreshToken(req: RefreshTokenRequest): Task[RefreshTokenResponse]
    }

    def authorize(req: AuthorizeRequest): RIO[AuthorizationModule, Unit] =
      ZIO.accessM(_.get.authorize(req))

    def requestToken(req: AccessTokenRequest): RIO[AuthorizationModule, AccessTokenResponse] =
      ZIO.accessM(_.get.requestToken(req))

    def refreshToken(req: RefreshTokenRequest): RIO[AuthorizationModule, RefreshTokenResponse] =
      ZIO.accessM(_.get.refreshToken(req))
  }

}
