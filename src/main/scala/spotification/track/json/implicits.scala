package spotification.track.json

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import io.circe.refined.refinedDecoder
import io.estatico.newtype.ops.toCoercibleIdOps
import spotification.common.SpotifyId
import spotification.track.{GetTrackResponse, TrackId}

object implicits {
  implicit val TrackIdDecoder: Decoder[TrackId] = implicitly[Decoder[SpotifyId]].map(_.coerce[TrackId])
  implicit val GetTrackResponseArtistDecoder: Decoder[GetTrackResponse.Artist] = deriveDecoder
  implicit val GetTrackResponseExternalUrlsDecoder: Decoder[GetTrackResponse.ExternalUrls] = deriveDecoder
  implicit val GetTrackResponseDecoder: Decoder[GetTrackResponse] = deriveDecoder
}
