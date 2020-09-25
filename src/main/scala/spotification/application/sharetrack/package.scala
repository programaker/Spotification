package spotification.application

import spotification.application.spotifyauthorization.{SpotifyAuthorizationEnv, requestAccessTokenProgram}
import spotification.domain.spotify.authorization.RefreshToken
import spotification.domain.spotify.track.{GetTrackRequest, TrackUri, makeShareTrackString}
import spotification.infra.spotify.track.TrackModule
import zio.{RIO, TaskLayer}

package object sharetrack {
  type ShareTrackEnv = TrackModule with SpotifyAuthorizationEnv
  object ShareTrackEnv {
    val live: TaskLayer[ShareTrackEnv] = TrackModule.live ++ SpotifyAuthorizationEnv.live
  }

  def shareTrackMessageProgram(refreshToken: RefreshToken, trackUri: TrackUri): RIO[ShareTrackEnv, String] =
    for {
      accessToken  <- requestAccessTokenProgram(refreshToken)
      getTrackResp <- TrackModule.getTrack(GetTrackRequest.make(trackUri, accessToken))
    } yield makeShareTrackString(getTrackResp)
}
