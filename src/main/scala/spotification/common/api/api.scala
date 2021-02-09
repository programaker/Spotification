package spotification.common

import cats.Applicative
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, HttpRoutes, Request, Response}
import spotification.authorization.RefreshToken
import spotification.authorization.api.requiredRefreshTokenFromRequest
import spotification.common.json.implicits.{GenericResponseErrorEncoder, entityEncoderF}
import zio.RIO
import zio.interop.catz.monadErrorInstance

package object api {
  type RoutesMapping[F[_]] = (String, HttpRoutes[F])
  type Routes[F[_]] = Seq[RoutesMapping[F]]

  def handleGenericError[F[_]: Applicative](dsl: Http4sDsl[F], e: Throwable): F[Response[F]] = {
    import dsl._
    InternalServerError(GenericResponse.Error(e.getMessage))
  }

  def doRequest[R, A, B](
    rawReq: Request[RIO[R, *]]
  )(
    f: (RefreshToken, A) => RIO[R, B]
  )(implicit
    D: EntityDecoder[RIO[R, *], A]
  ): RIO[R, B] =
    for {
      refreshToken <- requiredRefreshTokenFromRequest(rawReq)
      a            <- rawReq.as[A]
      b            <- f(refreshToken, a)
    } yield b

  def withDsl[R](f: Http4sDsl[RIO[R, *]] => HttpRoutes[RIO[R, *]]): HttpRoutes[RIO[R, *]] =
    f(Http4sDsl[RIO[R, *]])
}
