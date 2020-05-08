package spotification.presentation

import cats.Applicative
import org.http4s.Response
import org.http4s.dsl.Http4sDsl
import zio.RIO

object Presentation {
  def allRoutes[R <: PresentationModule]: Routes[RIO[R, *]] = Seq(
    "/health"        -> new HealthCheckController[R].routes,
    "/authorization" -> new AuthorizationController[R].routes
  )

  def handleGenericError[F[_]: Applicative](dsl: Http4sDsl[F], e: Throwable): F[Response[F]] = {
    import dsl._
    InternalServerError(e.getMessage)
  }
}
