package spotification.artist.json

import io.circe.Decoder
import io.circe.refined.refinedDecoder
import io.estatico.newtype.ops.toCoercibleIdOps
import spotification.artist.ArtistId
import spotification.common.SpotifyId

object implicits {
  implicit val ArtistIdDecoder: Decoder[ArtistId] = implicitly[Decoder[SpotifyId]].map(_.coerce[ArtistId])
}
