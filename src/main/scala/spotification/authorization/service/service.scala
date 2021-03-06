package spotification.authorization

import spotification.effect.accessServiceFunction
import zio.{Has, RIO, Task}

package object service {
  type RequestTokenService = AccessTokenRequest => Task[AccessTokenResponse]
  type RequestTokenServiceR = Has[RequestTokenService]
  def requestToken(req: AccessTokenRequest): RIO[RequestTokenServiceR, AccessTokenResponse] =
    accessServiceFunction(req)

  type RefreshTokenService = RefreshTokenRequest => Task[RefreshTokenResponse]
  type RefreshTokenServiceR = Has[RefreshTokenService]
  def refreshToken(req: RefreshTokenRequest): RIO[RefreshTokenServiceR, RefreshTokenResponse] =
    accessServiceFunction(req)
}
