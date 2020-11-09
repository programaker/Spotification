package spotification.track

import spotification.authorization.RefreshToken
import spotification.authorization.program.{SpotifyAuthorizationEnv, requestAccessTokenProgram}
import spotification.track.service.{TrackServiceEnv, getTrack}
import zio.RIO

package object program {
  type ShareTrackEnv = TrackServiceEnv with SpotifyAuthorizationEnv

  def makeShareTrackMessageProgram(refreshToken: RefreshToken, trackUri: TrackUri): RIO[ShareTrackEnv, String] =
    for {
      accessToken  <- requestAccessTokenProgram(refreshToken)
      getTrackResp <- getTrack(GetTrackRequest.make(trackUri, accessToken))
    } yield makeShareTrackString(getTrackResp)
}
