package spotification.track

import cats.Show
import eu.timepit.refined.auto._
import spotification.common.{NonBlankString, UriString}

final case class GetTrackResponse(
  artists: List[GetTrackResponse.Artist],
  external_urls: GetTrackResponse.ExternalUrls,
  name: NonBlankString
)
object GetTrackResponse {
  final case class Artist(name: NonBlankString)
  object Artist {
    implicit val GetTrackResponseArtistShow: Show[Artist] = (ar: Artist) => ar.name
  }

  final case class ExternalUrls(spotify: UriString)
}
