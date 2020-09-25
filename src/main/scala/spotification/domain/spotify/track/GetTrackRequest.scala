package spotification.domain.spotify.track

import spotification.domain.spotify.authorization.AccessToken

final case class GetTrackRequest(
  trackId: TrackId,
  accessToken: AccessToken
)
object GetTrackRequest {
  def make(trackUri: TrackUri, accessToken: AccessToken): GetTrackRequest =
    GetTrackRequest(
      trackId = trackIdFromUri(trackUri),
      accessToken = accessToken
    )

}
