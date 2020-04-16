package spotification.spotify.authorization

import zio.{Has, RIO, Task, ZIO}

/** ZIO module for Authorization */
object module {

  type Authorization = Has[AuthorizationService]

  trait AuthorizationService {
    def authorize(req: AuthorizeRequest): Task[Unit]
    def requestToken(req: AccessTokenRequest): Task[AccessTokenResponse]
    def refreshToken(req: RefreshTokenRequest): Task[RefreshTokenResponse]
  }

  def authorize(req: AuthorizeRequest): RIO[Authorization, Unit] =
    ZIO.accessM(_.get.authorize(req))

  def requestToken(req: AccessTokenRequest): RIO[Authorization, AccessTokenResponse] =
    ZIO.accessM(_.get.requestToken(req))

  def refreshToken(req: RefreshTokenRequest): RIO[Authorization, RefreshTokenResponse] =
    ZIO.accessM(_.get.refreshToken(req))

}
