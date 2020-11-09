package spotification.json

import cats.Applicative
import cats.effect.Sync
import io.circe.{Decoder, Encoder}
import io.circe.refined._
import io.estatico.newtype.ops._
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}
import spotification.authorization.{AccessToken, RefreshToken}
import spotification.common.{NonBlankString, SpotifyId}
import spotification.playlist.PlaylistId

object implicits {
  implicit def entityEncoderF[F[_]: Applicative, A: Encoder]: EntityEncoder[F, A] =
    jsonEncoderOf[F, A]

  implicit def entityDecoderF[F[_]: Sync, A: Decoder]: EntityDecoder[F, A] =
    jsonOf[F, A]

  implicit val AccessTokenEncoder: Encoder[AccessToken] =
    implicitly[Encoder[NonBlankString]].contramap(_.coerce[NonBlankString])

  implicit val AccessTokenDecoder: Decoder[AccessToken] =
    implicitly[Decoder[NonBlankString]].map(_.coerce[AccessToken])

  implicit val RefreshTokenEncoder: Encoder[RefreshToken] =
    implicitly[Encoder[NonBlankString]].contramap(_.coerce[NonBlankString])

  implicit val RefreshTokenDecoder: Decoder[RefreshToken] =
    implicitly[Decoder[NonBlankString]].map(_.coerce[RefreshToken])

  implicit val PlaylistIdDecoder: Decoder[PlaylistId] =
    implicitly[Decoder[SpotifyId]].map(_.coerce[PlaylistId])
}
