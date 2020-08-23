package spotification.presentation

import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import spotification.application.releaseradarnosingles._
import spotification.domain.spotify.playlist.PlaylistId
import zio.RIO
import io.circe.generic.auto._
import zio.interop.catz._
import spotification.infra.json.implicits._
import spotification.presentation._

final class ReleaseRadarNoSinglesController[R <: ReleaseRadarNoSinglesEnv] {
  private val H4sDsl: Http4sDsl[RIO[R, *]] = Http4sDsl[RIO[R, *]]
  import H4sDsl._

  val routes: HttpRoutes[RIO[R, *]] = HttpRoutes.of[RIO[R, *]] {
    case rawReq @ PATCH -> Root =>
      doRequest(rawReq) { (refreshToken, req: ReleaseRadarNoSinglesController.Request) =>
        releaseRadarNoSinglesProgram(refreshToken, req.releaseRadarId, req.releaseRadarNoSinglesId)
      }.foldM(
        handleGenericError(H4sDsl, _),
        _ => Ok(GenericResponse.Success("Enjoy your albums-only Release Radar!"))
      )
  }
}
object ReleaseRadarNoSinglesController {
  final case class Request(releaseRadarId: PlaylistId, releaseRadarNoSinglesId: PlaylistId)
}
