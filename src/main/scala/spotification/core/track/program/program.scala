package spotification.core.track

import spotification.core.authorization.RefreshToken
import spotification.core.authorization.program.{SpotifyAuthorizationEnv, requestAccessTokenProgram}
import spotification.core.track.service.{TrackServiceEnv, getTrack}
import zio.RIO

package object program {
  type ShareTrackEnv = TrackServiceEnv with SpotifyAuthorizationEnv

  def makeShareTrackMessageProgram(refreshToken: RefreshToken, trackUri: TrackUri): RIO[ShareTrackEnv, String] =
    for {
      accessToken  <- requestAccessTokenProgram(refreshToken)
      getTrackResp <- getTrack(GetTrackRequest.make(trackUri, accessToken))
    } yield makeShareTrackString(getTrackResp)
}
