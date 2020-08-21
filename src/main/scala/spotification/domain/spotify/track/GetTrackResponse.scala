package spotification.domain.spotify.track

import spotification.domain.{NonBlankString, UriString}

final case class GetTrackResponse(
  artists: List[GetTrackResponse.ArtistResponse],
  external_urls: GetTrackResponse.ExternalUrls,
  name: NonBlankString
)
object GetTrackResponse {
  final case class ArtistResponse(name: NonBlankString)
  final case class ExternalUrls(spotify: UriString)
}
