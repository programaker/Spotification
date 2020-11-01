package spotification.track

import spotification.authorization.AccessToken

final case class GetTrackRequest(
  trackId: TrackId,
  accessToken: AccessToken
)
object GetTrackRequest {
  def make(trackUri: TrackUri, accessToken: AccessToken): GetTrackRequest =
    GetTrackRequest(
      trackId = TrackId.fromUri(trackUri),
      accessToken = accessToken
    )
}
