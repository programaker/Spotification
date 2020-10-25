package spotification.common.infra.json

import cats.Applicative
import cats.effect.Sync
import io.circe.refined._
import io.circe.{Decoder, Encoder}
import io.estatico.newtype.ops._
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}
import spotification.spotify.authorization.{AccessToken, RefreshToken}
import spotification.spotify.playlist.PlaylistId
import spotification.common.{NonBlankString, SpotifyId}

object implicits {
  implicit def entityEncoderF[F[_]: Applicative, A: Encoder]: EntityEncoder[F, A] =
    jsonEncoderOf[F, A]

  implicit def entityDecoderF[F[_]: Sync, A: Decoder]: EntityDecoder[F, A] =
    jsonOf[F, A]

  implicit val accessTokenEncoder: Encoder[AccessToken] =
    implicitly[Encoder[NonBlankString]].contramap(_.coerce[NonBlankString])

  implicit val accessTokenDecoder: Decoder[AccessToken] =
    implicitly[Decoder[NonBlankString]].map(_.coerce[AccessToken])

  implicit val refreshTokenEncoder: Encoder[RefreshToken] =
    implicitly[Encoder[NonBlankString]].contramap(_.coerce[NonBlankString])

  implicit val refreshTokenDecoder: Decoder[RefreshToken] =
    implicitly[Decoder[NonBlankString]].map(_.coerce[RefreshToken])

  implicit val playlistIdDecoder: Decoder[PlaylistId] =
    implicitly[Decoder[SpotifyId]].map(_.coerce[PlaylistId])
}
