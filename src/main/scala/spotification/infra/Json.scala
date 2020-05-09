package spotification.infra

import cats.Applicative
import io.circe.{Decoder, Encoder}
import io.circe.refined._
import io.estatico.newtype.ops._
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf
import spotification.domain.{NonBlankString, SpotifyId}
import spotification.domain.spotify.authorization.{AccessToken, RefreshToken}
import spotification.domain.spotify.album.AlbumId
import eu.timepit.refined.auto._

object Json {
  object Implicits {
    implicit def apJsonEntityEncoder[F[_]: Applicative, A: Encoder]: EntityEncoder[F, A] =
      jsonEncoderOf[F, A]

    implicit val accessTokenEncoder: Encoder[AccessToken] =
      implicitly[Encoder[NonBlankString]].contramap(_.coerce[NonBlankString])

    implicit val accessTokenDecoder: Decoder[AccessToken] =
      implicitly[Decoder[NonBlankString]].map(_.coerce[AccessToken])

    implicit val refreshTokenEncoder: Encoder[RefreshToken] =
      implicitly[Encoder[NonBlankString]].contramap(_.coerce[NonBlankString])

    implicit val refreshTokenDecoder: Decoder[RefreshToken] =
      implicitly[Decoder[NonBlankString]].map(_.coerce[RefreshToken])

    implicit val albumIdDecoder: Decoder[AlbumId] =
      implicitly[Decoder[SpotifyId]].map(_.coerce[AlbumId])
  }
}
