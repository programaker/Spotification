package spotification.api

import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import spotification.json.implicits.entityEncoderF
import zio.RIO
import zio.interop.catz.{deferInstance, monadErrorInstance}

final class HealthCheckController[R] extends Http4sDsl[RIO[R, *]] {
  val routes: HttpRoutes[RIO[R, *]] = HttpRoutes.of[RIO[R, *]] {
    case GET -> Root => Ok(GenericResponse.Success("I'm doing well, thanks for asking ^_^"))
  }
}
