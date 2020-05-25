package spotification.presentation

import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import spotification.application.ReleaseRadarApp.fillReleaseRadarNoSinglesProgram
import spotification.application.ReleaseRadarAppEnv
import spotification.presentation.Controller.handleGenericError
import zio.RIO

// ==========
// Despite IntelliJ telling that
// `import zio.interop.catz._`
// `import spotification.infra.json._`
// are not being used, they are required to compile
// ==========
import zio.interop.catz._
import spotification.infra.Json.Implicits._

final class ReleaseRadarController[R <: ReleaseRadarAppEnv] {
  private val H4sDsl: Http4sDsl[RIO[R, *]] = Http4sDsl[RIO[R, *]]
  import H4sDsl._

  val routes: HttpRoutes[RIO[R, *]] = HttpRoutes.of[RIO[R, *]] {
    case POST -> Root =>
      fillReleaseRadarNoSinglesProgram.foldM(
        handleGenericError(H4sDsl, _),
        _ => Ok("The playlist is being filled...")
      )
  }
}