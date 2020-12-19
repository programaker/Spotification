package spotification.user.json

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import io.circe.refined.refinedEncoder
import io.circe.refined.refinedDecoder
import io.estatico.newtype.ops.toCoercibleIdOps
import spotification.common.SpotifyId
import spotification.user.{CreatePlaylistRequest, CreatePlaylistResponse, UserId}
import spotification.playlist.json.implicits.PlaylistIdDecoder

object implicits {
  implicit val UserIdEncoder: Encoder[UserId] = implicitly[Encoder[SpotifyId]].contramap(_.coerce[SpotifyId])
  implicit val UserIdDecoder: Decoder[UserId] = implicitly[Decoder[SpotifyId]].map(_.coerce[UserId])

  implicit val CreatePlaylistRequestBodyEncoder: Encoder[CreatePlaylistRequest.Body] = deriveEncoder
  implicit val CreatePlaylistResponseDecoder: Decoder[CreatePlaylistResponse] = deriveDecoder
}
