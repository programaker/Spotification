package spotification.domain.spotify

import spotification.domain.{base64, HexString32, NonBlankString}
import zio.{Has, RIO, ZIO}

package object authorization {

  type Authorization = Has[AuthorizationService]

  final case class AccessToken(value: NonBlankString) {
    override def toString: String = value.toString
  }

  final case class RefreshToken(value: NonBlankString) {
    override def toString: String = value.toString
  }

  final case class ClientId(value: HexString32) {
    override def toString: String = value.toString
  }

  final case class ClientSecret(value: HexString32) {
    override def toString: String = value.toString
  }

  final case class Credentials(clientId: ClientId, clientSecret: ClientSecret)

  def authorize(req: AuthorizeRequest): RIO[Authorization, Unit] =
    ZIO.accessM(_.get.authorize(req))

  def requestToken(req: AccessTokenRequest): RIO[Authorization, AccessTokenResponse] =
    ZIO.accessM(_.get.requestToken(req))

  def refreshToken(req: RefreshTokenRequest): RIO[Authorization, RefreshTokenResponse] =
    ZIO.accessM(_.get.refreshToken(req))

  def base64Credentials(credentials: Credentials): String =
    base64(s"${credentials.clientId}:${credentials.clientSecret}")

}
