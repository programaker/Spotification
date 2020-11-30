package spotification.track

import spotification.authorization.AccessToken

final case class GetTrackRequest(
  accessToken: AccessToken,
  trackId: TrackId
)
object GetTrackRequest {
  def make(accessToken: AccessToken, trackUri: TrackUri): GetTrackRequest =
    GetTrackRequest(
      accessToken = accessToken,
      trackId = TrackId.fromUri(trackUri)
    )
}
