package spotification.domain.spotify.track

import spotification.domain.NonBlankString

final case class GetTrackResponse(
  artists: List[GetTrackResponse.ArtistResponse],
  name: NonBlankString
)
object GetTrackResponse {
  final case class ArtistResponse(name: NonBlankString)
}
