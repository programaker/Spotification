package spotification.track

import org.http4s.HttpRoutes
import spotification.authorization.api.{RequestAccessTokenProgramLayer, requiredRefreshTokenFromRequest}
import spotification.common.GenericResponse
import spotification.common.api.{handleGenericError, withDsl}
import spotification.common.httpclient.HttpClientR
import spotification.common.json.implicits.{GenericResponseSuccessEncoder, entityEncoderF}
import spotification.config.service.{AuthorizationConfigR, TrackConfigR}
import spotification.track.httpclient.GetTrackServiceLayer
import spotification.track.program.{MakeShareTrackMessageProgramR, TrackProgramsR, makeShareTrackMessageProgram}
import zio.interop.catz.{deferInstance, monadErrorInstance}
import zio.{RIO, RLayer}

package object api {
  val MakeShareTrackMessageProgramLayer
    : RLayer[AuthorizationConfigR with HttpClientR with TrackConfigR, MakeShareTrackMessageProgramR] =
    RequestAccessTokenProgramLayer ++ GetTrackServiceLayer

  val TracksLayer: RLayer[AuthorizationConfigR with HttpClientR with TrackConfigR, TrackProgramsR] =
    MakeShareTrackMessageProgramLayer

  def makeTracksApi[R <: TrackProgramsR]: HttpRoutes[RIO[R, *]] = withDsl { dsl =>
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
