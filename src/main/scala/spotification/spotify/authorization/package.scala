package spotification.spotify

import java.nio.charset.StandardCharsets.UTF_8
import java.util.Base64

import zio.{Has, RIO, Task, ZIO}

package object authorization {

  type Authorization = Has[AuthorizationService]

  trait AuthorizationService {
    def authorize(req: AuthorizeRequest): Task[Unit]
    def requestToken(req: TokenRequest): Task[TokenResponse]
    def refreshToken(req: RefreshTokenRequest): Task[RefreshTokenResponse]
  }

  def authorize(req: AuthorizeRequest): RIO[Authorization, Unit] =
    ZIO.accessM(_.get.authorize(req))

  def requestToken(req: TokenRequest): RIO[Authorization, TokenResponse] =
    ZIO.accessM(_.get.requestToken(req))

  def refreshToken(req: RefreshTokenRequest): RIO[Authorization, RefreshTokenResponse] =
    ZIO.accessM(_.get.refreshToken(req))

  def base64Credentials(credentials: Credentials): String =
    Base64.getEncoder.encodeToString(s"${credentials.clientId}:${credentials.clientSecret}".getBytes(UTF_8))

  def authorizationBasicHeader(credentials: Credentials): String =
    s"Authorization: Basic ${base64Credentials(credentials)}"

  def authorizationBearerHeader(accessToken: AccessToken): String =
    s"Authorization: Bearer ${accessToken.value}"

}
