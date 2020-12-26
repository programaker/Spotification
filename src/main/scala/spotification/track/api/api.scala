package spotification.track

import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import spotification.authorization.api.{RequestAccessTokenProgramLayer, requiredRefreshTokenFromRequest}
import spotification.common.GenericResponse
import spotification.common.api.handleGenericError
import spotification.common.httpclient.HttpClientEnv
import spotification.common.json.implicits.{GenericResponseSuccessEncoder, entityEncoderF}
import spotification.config.service.{AuthorizationConfigEnv, TrackConfigEnv}
import spotification.track.httpclient.GetTrackServiceLayer
import spotification.track.program.{MakeShareTrackMessageProgramEnv, TracksEnv, makeShareTrackMessageProgram}
import zio.interop.catz.{deferInstance, monadErrorInstance}
import zio.{RIO, RLayer}

package object api {
  val MakeShareTrackMessageProgramLayer
    : RLayer[AuthorizationConfigEnv with HttpClientEnv with TrackConfigEnv, MakeShareTrackMessageProgramEnv] =
    RequestAccessTokenProgramLayer ++ GetTrackServiceLayer

  val TracksLayer: RLayer[AuthorizationConfigEnv with HttpClientEnv with TrackConfigEnv, TracksEnv] =
    MakeShareTrackMessageProgramLayer

  def tracksApi[R <: TracksEnv]: HttpRoutes[RIO[R, *]] = {
    val dsl: Http4sDsl[RIO[R, *]] = Http4sDsl[RIO[R, *]]
    import dsl._

    HttpRoutes.of[RIO[R, *]] { case rawReq @ GET -> Root / TrackUriVar(trackUri) / "share-message" =>
      requiredRefreshTokenFromRequest(rawReq)
        .flatMap(makeShareTrackMessageProgram(_, trackUri))
        .foldM(
          handleGenericError(dsl, _),
          message => Ok(GenericResponse.Success(message))
        )
    }
  }
}
