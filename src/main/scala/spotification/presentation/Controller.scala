package spotification.presentation

import cats.Applicative
import org.http4s.Response
import org.http4s.dsl.Http4sDsl
import io.circe.generic.auto._
import spotification.infra.Json.Implicits._

object Controller {
  def handleGenericError[F[_]: Applicative](dsl: Http4sDsl[F], e: Throwable): F[Response[F]] = {
    import dsl._
    InternalServerError(GenericResponse.Error(e.getMessage))
  }
}
