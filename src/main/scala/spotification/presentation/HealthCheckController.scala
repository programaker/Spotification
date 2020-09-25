package spotification.presentation

import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import zio.RIO
import zio.interop.catz.{deferInstance, monadErrorInstance}
import io.circe.generic.auto._
import spotification.infra.json.implicits.entityEncoderF

final class HealthCheckController[R] {
  private val h4sDsl: Http4sDsl[RIO[R, *]] = Http4sDsl[RIO[R, *]]
  import h4sDsl._

  val routes: HttpRoutes[RIO[R, *]] = HttpRoutes.of[RIO[R, *]] {
    case GET -> Root => Ok(GenericResponse.Success("I'm doing well, thanks for asking ^_^"))
  }
}
