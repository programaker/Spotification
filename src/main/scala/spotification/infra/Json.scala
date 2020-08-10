package spotification.infra

import cats.Applicative
import cats.effect.Sync
import cats.implicits._
import io.circe.{Decoder, Encoder}
import io.circe.refined._
import io.circe.generic.auto._
import io.estatico.newtype.ops._
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.jsonEncoderOf
import org.http4s.circe.jsonOf
import spotification.domain.{NonBlankString, SpotifyId}
import spotification.domain.spotify.authorization.{AccessToken, RefreshToken}
import eu.timepit.refined.auto._
import spotification.domain.spotify.playlist._
import spotification.domain.spotify.track.GetTrackResponse

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

    implicit val PlaylistIdDecoder: Decoder[PlaylistId] =
      implicitly[Decoder[SpotifyId]].map(_.coerce[PlaylistId])

    implicit val GetPlaylistsItemsResponseDecoder: Decoder[GetPlaylistsItemsResponse] =
      Decoder[GetPlaylistsItemsResponse.Success].widen or
        Decoder[GetPlaylistsItemsResponse.Error].widen

    implicit val AddItemsToPlaylistResponseDecoder: Decoder[AddItemsToPlaylistResponse] =
      Decoder[AddItemsToPlaylistResponse.Success].widen or
        Decoder[AddItemsToPlaylistResponse.Error].widen

    implicit val RemoveItemsFromPlaylistResponseDecoder: Decoder[RemoveItemsFromPlaylistResponse] =
      Decoder[RemoveItemsFromPlaylistResponse.Success].widen or
        Decoder[RemoveItemsFromPlaylistResponse.Error].widen

    implicit val GetTrackResponseDecoder: Decoder[GetTrackResponse] =
      Decoder[GetTrackResponse.Success].widen or
        Decoder[GetTrackResponse.Error].widen
  }
}
