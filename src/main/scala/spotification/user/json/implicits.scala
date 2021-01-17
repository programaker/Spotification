package spotification.user.json

import io.circe.refined.{refinedDecoder, refinedEncoder}
import io.circe.{Decoder, Encoder}
import io.estatico.newtype.ops.toCoercibleIdOps
import spotification.common.SpotifyId
import spotification.user.UserId

object implicits {
  implicit val UserIdEncoder: Encoder[UserId] = implicitly[Encoder[SpotifyId]].contramap(_.coerce[SpotifyId])
  implicit val UserIdDecoder: Decoder[UserId] = implicitly[Decoder[SpotifyId]].map(_.coerce[UserId])
}
