package spotification.domain.spotify.track

import spotification.domain.spotify.authorization.AccessToken

final case class GetTrackRequest(
  trackId: TrackId,
  accessToken: AccessToken
)
