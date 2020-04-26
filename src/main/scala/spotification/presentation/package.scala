package spotification

import cats.Applicative
import org.http4s.Response
import org.http4s.dsl.Http4sDsl
import zio.{Layer, ZLayer}

package object presentation extends AuthorizationM with PresentationM {

  val routesLayer: Layer[Throwable, PresentationModule] = ZLayer.fromEffect(PresentationModule.allRoutes)

  def handleGenericError[F[_]: Applicative](dsl: Http4sDsl[F], e: Throwable): F[Response[F]] = {
    import dsl._
    InternalServerError(e.getMessage)
  }

}
