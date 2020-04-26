package spotification.presentation

import cats.Applicative
import org.http4s.Response
import org.http4s.dsl.Http4sDsl

object Presentation {
  val allRoutes: Routes[PresentationIO] = Seq(
    "/authorization" -> AuthorizationController.routes
  )

  private[presentation] def handleGenericError[F[_]: Applicative](dsl: Http4sDsl[F], e: Throwable): F[Response[F]] = {
    import dsl._
    InternalServerError(e.getMessage)
  }
}
