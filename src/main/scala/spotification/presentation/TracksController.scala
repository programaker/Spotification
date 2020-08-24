package spotification.presentation

import eu.timepit.refined._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import spotification.application.sharetrack.{ShareTrackEnv, shareTrackMessageProgram}
import spotification.domain.spotify.track.{TrackUri, TrackUriR}
import spotification.presentation.TracksController.TrackUriVar
import zio.RIO
import zio.interop.catz.{deferInstance, monadErrorInstance}
import io.circe.generic.auto._
import spotification.infra.json.implicits.FEntityEncoder

final class TracksController[R <: ShareTrackEnv] {
  private val H4sDsl: Http4sDsl[RIO[R, *]] = Http4sDsl[RIO[R, *]]
  import H4sDsl._

  val routes: HttpRoutes[RIO[R, *]] = HttpRoutes.of[RIO[R, *]] {
    case rawReq @ GET -> Root / TrackUriVar(trackUri) / "share-message" =>
      requiredRefreshTokenFromRequest(rawReq)
        .flatMap(shareTrackMessageProgram(_, trackUri))
        .foldM(
          handleGenericError(H4sDsl, _),
          message => Ok(GenericResponse.Success(message))
        )
  }
}
object TracksController {
  object TrackUriVar {
    def unapply(pathVar: String): Option[TrackUri] = refineV[TrackUriR](pathVar).toOption
  }
}