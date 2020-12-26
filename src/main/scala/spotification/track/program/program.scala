package spotification.track

import spotification.authorization.RefreshToken
import spotification.authorization.program.{RequestAccessTokenProgramEnv, requestAccessTokenProgram}
import spotification.track.service.{GetTrackServiceEnv, getTrack}
import zio.RIO

package object program {
  type TracksEnv = MakeShareTrackMessageProgramEnv
  type MakeShareTrackMessageProgramEnv = RequestAccessTokenProgramEnv with GetTrackServiceEnv

  def makeShareTrackMessageProgram(
    refreshToken: RefreshToken,
    trackUri: TrackUri
  ): RIO[MakeShareTrackMessageProgramEnv, String] =
    for {
      accessToken  <- requestAccessTokenProgram(refreshToken)
      getTrackResp <- getTrack(GetTrackRequest.make(accessToken, trackUri))
    } yield makeShareTrackString(getTrackResp)
}
