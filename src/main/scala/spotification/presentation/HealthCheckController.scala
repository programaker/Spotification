package spotification.presentation

import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import zio.RIO
import zio.interop.catz._

final class HealthCheckController[R] {
  private val H4sHealthCheckDsl: Http4sDsl[RIO[R, *]] = Http4sDsl[RIO[R, *]]
  import H4sHealthCheckDsl._

  val routes: HttpRoutes[RIO[R, *]] = HttpRoutes.of[RIO[R, *]] {
    case GET -> Root => Ok("I'm doing well, thanks for asking ^_^")
  }
}
