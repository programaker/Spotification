package spotification.album.json

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import io.circe.refined.refinedDecoder
import io.estatico.newtype.ops.toCoercibleIdOps
import spotification.album.{AlbumId, GetAlbumSampleTrackResponse}
import spotification.common.SpotifyId

object implicits {
  implicit val AlbumIdDecoder: Decoder[AlbumId] = implicitly[Decoder[SpotifyId]].map(_.coerce[AlbumId])
  implicit val GetAlbumSampleTrackResponseDecoder: Decoder[GetAlbumSampleTrackResponse] = deriveDecoder
}
