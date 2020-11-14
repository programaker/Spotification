package spotification.track

import spotification.authorization.api.{SpotifyAuthorizationLayer, requiredRefreshTokenFromRequest}
import spotification.track.httpclient.TrackServiceLayer
import zio.TaskLayer
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import io.circe.generic.auto._
import spotification.common.api.{GenericResponse, handleGenericError}
import spotification.track.program.{ShareTrackEnv, makeShareTrackMessageProgram}
import spotification.json.implicits._
import zio.RIO
import zio.interop.catz.{deferInstance, monadErrorInstance}

package object api {
  val ShareTrackLayer: TaskLayer[ShareTrackEnv] =
    TrackServiceLayer ++ SpotifyAuthorizationLayer

  def makeTracksRoutes[R <: ShareTrackEnv]: HttpRoutes[RIO[R, *]] = {
    val dsl: Http4sDsl[RIO[R, *]] = Http4sDsl[RIO[R, *]]
    import dsl._

    HttpRoutes.of[RIO[R, *]] {
      case rawReq @ GET -> Root / TrackUriVar(trackUri) / "share-message" =>
        requiredRefreshTokenFromRequest(rawReq)
          .flatMap(makeShareTrackMessageProgram(_, trackUri))
          .foldM(
            handleGenericError(dsl, _),
            message => Ok(GenericResponse.Success(message))
          )
    }
  }
}
