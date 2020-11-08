package spotification.api

import eu.timepit.refined._
import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import spotification.api.TracksController.TrackUriVar
import spotification.core.track.{TrackUri, TrackUriR}
import spotification.core.track.program.{ShareTrackEnv, makeShareTrackMessageProgram}
import spotification.json.implicits.entityEncoderF
import zio.RIO
import zio.interop.catz.{deferInstance, monadErrorInstance}

final class TracksController[R <: ShareTrackEnv] extends Http4sDsl[RIO[R, *]] {
  val routes: HttpRoutes[RIO[R, *]] = HttpRoutes.of[RIO[R, *]] {
    case rawReq @ GET -> Root / TrackUriVar(trackUri) / "share-message" =>
      requiredRefreshTokenFromRequest(rawReq)
        .flatMap(makeShareTrackMessageProgram(_, trackUri))
        .foldM(
          handleGenericError(this, _),
          message => Ok(GenericResponse.Success(message))
        )
  }
}
object TracksController {
  object TrackUriVar {
    def unapply(pathVar: String): Option[TrackUri] = refineV[TrackUriR](pathVar).toOption
  }
}
