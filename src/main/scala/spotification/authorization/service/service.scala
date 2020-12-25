package spotification.authorization

import zio.{Has, RIO, Task, ZIO}

package object service {
  type RequestTokenService = AccessTokenRequest => Task[AccessTokenResponse]
  type RequestTokenServiceEnv = Has[RequestTokenService]
  def requestToken(req: AccessTokenRequest): RIO[RequestTokenServiceEnv, AccessTokenResponse] =
    ZIO.accessM(_.get.apply(req))

  type RefreshTokenService = RefreshTokenRequest => Task[RefreshTokenResponse]
  type RefreshTokenServiceEnv = Has[RefreshTokenService]
  def refreshToken(req: RefreshTokenRequest): RIO[RefreshTokenServiceEnv, RefreshTokenResponse] =
    ZIO.accessM(_.get.apply(req))
}
