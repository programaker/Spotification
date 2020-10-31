package spotification.spotify.track

import spotification.spotify.authorization.RefreshToken
import spotification.spotify.authorization.application.spotifyauthorizarion.{
  SpotifyAuthorizationEnv,
  requestAccessTokenProgram
}
import zio.{Has, RIO, ZIO}

package object application {
  type TrackServiceEnv = Has[TrackService]
  type ShareTrackEnv = TrackServiceEnv with SpotifyAuthorizationEnv

  def getTrack(req: GetTrackRequest): RIO[TrackServiceEnv, GetTrackResponse] =
    ZIO.accessM(_.get.getTrack(req))

  def shareTrackMessageProgram(refreshToken: RefreshToken, trackUri: TrackUri): RIO[ShareTrackEnv, String] =
    for {
      accessToken  <- requestAccessTokenProgram(refreshToken)
      getTrackResp <- getTrack(GetTrackRequest.make(trackUri, accessToken))
    } yield makeShareTrackString(getTrackResp)
}
