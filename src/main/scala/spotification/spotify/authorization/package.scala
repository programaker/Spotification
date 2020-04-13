package spotification.spotify

import zio.{Has, IO, RIO, Task, ZIO}

package object authorization {
  type Authorization = Has[AuthorizationService]

  trait AuthorizationService {
    def authorize(req: AuthorizationRequest): IO[AuthorizationError, AuthorizationResponse]
    def requestToken(req: TokenRequest): Task[TokenResponse]
    def refreshToken(req: RefreshTokenRequest): Task[RefreshTokenResponse]
  }

  def authorize(req: AuthorizationRequest): ZIO[Authorization, AuthorizationError, AuthorizationResponse] =
    ZIO.accessM(_.get.authorize(req))

  def requestToken(req: TokenRequest): RIO[Authorization, TokenResponse] =
    ZIO.accessM(_.get.requestToken(req))

  def refreshToken(req: RefreshTokenRequest): RIO[Authorization, RefreshTokenResponse] =
    ZIO.accessM(_.get.refreshToken(req))
}
