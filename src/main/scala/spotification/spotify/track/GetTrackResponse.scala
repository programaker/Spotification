package spotification.spotify.track

import cats.Show
import eu.timepit.refined.auto._
import spotification.common.{NonBlankString, UriString}

final case class GetTrackResponse(
  artists: List[GetTrackResponse.ArtistResponse],
  external_urls: GetTrackResponse.ExternalUrls,
  name: NonBlankString
)
object GetTrackResponse {
  final case class ArtistResponse(name: NonBlankString)
  object ArtistResponse {
    implicit val ArtistResponseShow: Show[ArtistResponse] = (ar: ArtistResponse) => ar.name
  }

  final case class ExternalUrls(spotify: UriString)
}
