package spotification.track

import spotification.authorization.RefreshToken
import spotification.authorization.program.{RequestAccessTokenProgramR, requestAccessTokenProgram}
import spotification.track.service.{GetTrackServiceR, getTrack}
import zio.RIO

package object program {
  type MakeShareTrackMessageProgramR = RequestAccessTokenProgramR with GetTrackServiceR
  type TrackProgramsR = MakeShareTrackMessageProgramR

  def makeShareTrackMessageProgram(
    refreshToken: RefreshToken,
    trackUri: TrackUri
  ): RIO[MakeShareTrackMessageProgramR, String] =
    for {
      accessToken  <- requestAccessTokenProgram(refreshToken)
      getTrackResp <- getTrack(GetTrackRequest.make(accessToken, trackUri))
    } yield makeShareTrackString(getTrackResp)
}
