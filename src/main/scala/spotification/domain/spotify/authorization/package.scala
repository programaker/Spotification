package spotification.domain.spotify

import java.nio.charset.StandardCharsets.UTF_8
import java.util.Base64

import zio.{Has, RIO, ZIO}

package object authorization {

  type Authorization = Has[AuthorizationService]

  def authorize(req: AuthorizeRequest): RIO[Authorization, Unit] =
    ZIO.accessM(_.get.authorize(req))

  def requestToken(req: AccessTokenRequest): RIO[Authorization, AccessTokenResponse] =
    ZIO.accessM(_.get.requestToken(req))

  def refreshToken(req: RefreshTokenRequest): RIO[Authorization, RefreshTokenResponse] =
    ZIO.accessM(_.get.refreshToken(req))

  def base64Credentials(credentials: Credentials): String =
    Base64.getEncoder.encodeToString(s"${credentials.clientId}:${credentials.clientSecret}".getBytes(UTF_8))

}
