package spotification.infra

import cats.Applicative
import cats.effect.Sync
import io.circe.refined._
import io.circe.{Decoder, Encoder}
import io.estatico.newtype.ops._
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}
import spotification.domain.spotify.ErrorResponse
import spotification.domain.spotify.authorization.{AccessToken, RefreshToken}
import spotification.domain.spotify.playlist.{GetPlaylistsItemsResponse, PlaylistId, PlaylistSnapshotResponse}
import spotification.domain.spotify.track.GetTrackResponse
import spotification.domain.{NonBlankString, SpotifyId}

object Json {
  object Implicits {
    implicit def FEntityEncoder[F[_]: Applicative, A: Encoder]: EntityEncoder[F, A] =
      jsonEncoderOf[F, A]

    implicit def FEntityDecoder[F[_]: Sync, A: Decoder]: EntityDecoder[F, A] =
      jsonOf[F, A]

    implicit val AccessTokenEncoder: Encoder[AccessToken] =
      implicitly[Encoder[NonBlankString]].contramap(_.coerce[NonBlankString])

    implicit val AccessTokenDecoder: Decoder[AccessToken] =
      implicitly[Decoder[NonBlankString]].map(_.coerce[AccessToken])

    implicit val RefreshTokenEncoder: Encoder[RefreshToken] =
      implicitly[Encoder[NonBlankString]].contramap(_.coerce[NonBlankString])

    implicit val RefreshTokenDecoder: Decoder[RefreshToken] =
      implicitly[Decoder[NonBlankString]].map(_.coerce[RefreshToken])

    implicit val ErrorResponseDecoder: Decoder[ErrorResponse] =
      Decoder[ErrorResponse]

    implicit val PlaylistIdDecoder: Decoder[PlaylistId] =
      implicitly[Decoder[SpotifyId]].map(_.coerce[PlaylistId])

    implicit val GetPlaylistsItemsResponseDecoder: Decoder[GetPlaylistsItemsResponse] =
      Decoder[GetPlaylistsItemsResponse]

    implicit val PlaylistSnapshotResponseDecoder: Decoder[PlaylistSnapshotResponse] =
      Decoder[PlaylistSnapshotResponse]

    implicit val GetTrackResponseDecoder: Decoder[GetTrackResponse] =
      Decoder[GetTrackResponse]
  }
}
