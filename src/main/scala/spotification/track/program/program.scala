package spotification.track

import spotification.authorization.RefreshToken
import spotification.authorization.program.{SpotifyAuthorizationEnv, requestAccessTokenProgram}
import spotification.track.service.{GetTrackServiceEnv, getTrack}
import zio.RIO

package object program {
  type ShareTrackEnv = GetTrackServiceEnv with SpotifyAuthorizationEnv

  def makeShareTrackMessageProgram(refreshToken: RefreshToken, trackUri: TrackUri): RIO[ShareTrackEnv, String] =
    for {
      accessToken  <- requestAccessTokenProgram(refreshToken)
      getTrackResp <- getTrack(GetTrackRequest.make(accessToken, trackUri))
    } yield makeShareTrackString(getTrackResp)
}
