package spotification.domain.spotify

import spotification.domain.{base64, HexString32, NonBlankString, Val}
import zio.{Has, RIO, ZIO}
import cats.implicits._

package object authorization {

  type Authorization = Has[AuthorizationService]

  final case class AccessToken(value: NonBlankString) extends Val[NonBlankString]
  final case class RefreshToken(value: NonBlankString) extends Val[NonBlankString]

  final case class ClientId(value: HexString32) extends Val[HexString32]
  final case class ClientSecret(value: HexString32) extends Val[HexString32]
  final case class Credentials(clientId: ClientId, clientSecret: ClientSecret)

  def authorize(req: AuthorizeRequest): RIO[Authorization, Unit] =
    ZIO.accessM(_.get.authorize(req))

  def requestToken(req: AccessTokenRequest): RIO[Authorization, AccessTokenResponse] =
    ZIO.accessM(_.get.requestToken(req))

  def refreshToken(req: RefreshTokenRequest): RIO[Authorization, RefreshTokenResponse] =
    ZIO.accessM(_.get.refreshToken(req))

  def base64Credentials(credentials: Credentials): String =
    base64(show"${credentials.clientId}:${credentials.clientSecret}")

}
