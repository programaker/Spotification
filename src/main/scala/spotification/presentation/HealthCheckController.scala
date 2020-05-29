package spotification.presentation

import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import zio.RIO

// ==========
// Despite IntelliJ telling that
// `import io.circe.generic.auto._`
// `import zio.interop.catz._`
// `import spotification.infra.json._`
// are not being used, they are required to compile
// ==========
import io.circe.generic.auto._
import zio.interop.catz._
import spotification.infra.Json.Implicits._

final class HealthCheckController[R] {
  private val H4sHealthCheckDsl: Http4sDsl[RIO[R, *]] = Http4sDsl[RIO[R, *]]
  import H4sHealthCheckDsl._

  val routes: HttpRoutes[RIO[R, *]] = HttpRoutes.of[RIO[R, *]] {
    case GET -> Root => Ok(GenericResponse.Success("I'm doing well, thanks for asking ^_^"))
  }
}
