package spotification.domain.spotify

import spotification.domain.{HexString32, NonBlankString, Val}
import zio.{Has, RIO, ZIO}
import cats.implicits._
import eu.timepit.refined.cats._

package object authorization {

  final case class AccessToken(value: NonBlankString) extends Val[NonBlankString]
  final case class RefreshToken(value: NonBlankString) extends Val[NonBlankString]

  final case class ClientId(value: HexString32) extends Val[HexString32]
  final case class ClientSecret(value: HexString32) extends Val[HexString32]

  type Authorization = Has[AuthorizationService]

  def authorize(req: AuthorizeRequest): RIO[Authorization, Unit] =
    ZIO.accessM(_.get.authorize(req))

  def requestToken(req: AccessTokenRequest): RIO[Authorization, AccessTokenResponse] =
    ZIO.accessM(_.get.requestToken(req))

  def refreshToken(req: RefreshTokenRequest): RIO[Authorization, RefreshTokenResponse] =
    ZIO.accessM(_.get.refreshToken(req))

}
