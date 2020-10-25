package spotification.spotify.track

import cats.Show
import eu.timepit.refined.auto._
import spotification.domain.{NonBlankString, UriString}

final case class GetTrackResponse(
  artists: List[GetTrackResponse.ArtistResponse],
  external_urls: GetTrackResponse.ExternalUrls,
  name: NonBlankString
)
object GetTrackResponse {
  final case class ArtistResponse(name: NonBlankString)
  object ArtistResponse {
    implicit val artistResponseShow: Show[ArtistResponse] = (ar: ArtistResponse) => ar.name
  }

  final case class ExternalUrls(spotify: UriString)
}
