package spotification.spotify.track.application

import spotification.spotify.authorization.RefreshToken
import spotification.spotify.authorization.application.spotifyauthorizarion.{
  SpotifyAuthorizationEnv,
  requestAccessTokenProgram
}
import spotification.spotify.track.{GetTrackRequest, TrackUri, makeShareTrackString}
import zio.{RIO, TaskLayer}

package object sharetrack {
  type ShareTrackEnv = TrackModule with SpotifyAuthorizationEnv
  object ShareTrackEnv {
    val live: TaskLayer[ShareTrackEnv] = TrackModule.live ++ SpotifyAuthorizationEnv.live
  }

  def shareTrackMessageProgram(refreshToken: RefreshToken, trackUri: TrackUri): RIO[ShareTrackEnv, String] =
    for {
      accessToken  <- requestAccessTokenProgram(refreshToken)
      getTrackResp <- getTrack(GetTrackRequest.make(trackUri, accessToken))
    } yield makeShareTrackString(getTrackResp)
}
