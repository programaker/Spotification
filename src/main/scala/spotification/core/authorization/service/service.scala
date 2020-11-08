package spotification.core.authorization

import zio.{Has, RIO, ZIO}

package object service {
  type AuthorizationServiceEnv = Has[AuthorizationService]

  def requestToken(req: AccessTokenRequest): RIO[AuthorizationServiceEnv, AccessTokenResponse] =
    ZIO.accessM(_.get.requestToken(req))

  def refreshToken(req: RefreshTokenRequest): RIO[AuthorizationServiceEnv, RefreshTokenResponse] =
    ZIO.accessM(_.get.refreshToken(req))
}
