package spotification.presentation

import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import spotification.application.ReleaseRadarNoSinglesApp.fillReleaseRadarNoSinglesProgram
import spotification.application.ReleaseRadarNoSinglesAppEnv
import spotification.domain.spotify.playlist.PlaylistId
import spotification.presentation.Controller.handleGenericError
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

final class ReleaseRadarNoSinglesController[R <: ReleaseRadarNoSinglesAppEnv] {
  private val H4sDsl: Http4sDsl[RIO[R, *]] = Http4sDsl[RIO[R, *]]
  import H4sDsl._

  val routes: HttpRoutes[RIO[R, *]] = HttpRoutes.of[RIO[R, *]] {
    case rawReq @ PATCH -> Root =>
      for {
        req <- rawReq.as[ReleaseRadarNoSinglesController.Request]

        resp <- fillReleaseRadarNoSinglesProgram(req.releaseRadarId, req.releaseRadarNoSinglesId).foldM(
          handleGenericError(H4sDsl, _),
          _ => Ok(GenericResponse.Success("Enjoy your albums-only Release Radar!"))
        )
      } yield resp
  }
}
object ReleaseRadarNoSinglesController {
  final case class Request(releaseRadarId: PlaylistId, releaseRadarNoSinglesId: PlaylistId)
}
