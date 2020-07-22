package spotification.presentation

import cats.Applicative
import cats.implicits._
import io.circe.generic.auto._
import org.http4s.headers.Authorization
import org.http4s.dsl.Http4sDsl
import org.http4s.{Request, Response}
import spotification.domain.NonBlankStringR
import spotification.domain.spotify.authorization.RefreshToken
import spotification.infra.Infra.refineRIO
import spotification.infra.Json.Implicits._
import zio.{RIO, ZIO}
import zio.interop.catz._

object Controller {
  def handleGenericError[F[_]: Applicative](dsl: Http4sDsl[F], e: Throwable): F[Response[F]] = {
    import dsl._
    InternalServerError(GenericResponse.Error(e.getMessage))
  }

  def requiredRefreshTokenFromRequest[R](req: Request[RIO[R, *]]): RIO[R, RefreshToken] =
    refreshTokenFromRequest(req).flatMap {
      case Some(refreshToken) => ZIO.succeed(refreshToken)
      case None               => ZIO.fail(new Exception("Refresh token absent from request"))
    }

  def refreshTokenFromRequest[R](req: Request[RIO[R, *]]): RIO[R, Option[RefreshToken]] =
    req.headers
      .get(Authorization)
      .map(_.value)
      .map(refineRIO[R, NonBlankStringR](_))
      .sequence
      .map(_.map(RefreshToken(_)))
}
