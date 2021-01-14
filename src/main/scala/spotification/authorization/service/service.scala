package spotification.authorization

import zio.{Has, RIO, Task, ZIO}

package object service {
  type RequestTokenService = AccessTokenRequest => Task[AccessTokenResponse]
  type RequestTokenServiceR = Has[RequestTokenService]

  type RefreshTokenService = RefreshTokenRequest => Task[RefreshTokenResponse]
  type RefreshTokenServiceR = Has[RefreshTokenService]

  def requestToken(req: AccessTokenRequest): RIO[RequestTokenServiceR, AccessTokenResponse] =
    ZIO.accessM(_.get.apply(req))

  def refreshToken(req: RefreshTokenRequest): RIO[RefreshTokenServiceR, RefreshTokenResponse] =
    ZIO.accessM(_.get.apply(req))
}
