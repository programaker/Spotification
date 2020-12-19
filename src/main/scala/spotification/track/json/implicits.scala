package spotification.track.json

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import io.circe.refined.refinedDecoder
import spotification.track.GetTrackResponse

object implicits {
  implicit val GetTrackResponseArtistDecoder: Decoder[GetTrackResponse.Artist] = deriveDecoder
  implicit val GetTrackResponseExternalUrlsDecoder: Decoder[GetTrackResponse.ExternalUrls] = deriveDecoder
  implicit val GetTrackResponseDecoder: Decoder[GetTrackResponse] = deriveDecoder
}
