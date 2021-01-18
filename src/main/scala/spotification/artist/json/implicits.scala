package spotification.artist.json

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import spotification.artist.{ArtistId, GetArtistsAlbumsResponse, GetMyFollowedArtistsResponse}
import io.circe.refined.refinedDecoder
import io.estatico.newtype.ops.toCoercibleIdOps
import spotification.common.SpotifyId
import spotification.album.json.implicits.AlbumIdDecoder

object implicits {
  implicit val ArtistIdDecoder: Decoder[ArtistId] = implicitly[Decoder[SpotifyId]].map(_.coerce[ArtistId])

  implicit val GetArtistsAlbumsAlbumDecoder: Decoder[GetArtistsAlbumsResponse.Album] = deriveDecoder
  implicit val GetArtistsAlbumsResponseDecoder: Decoder[GetArtistsAlbumsResponse] = deriveDecoder

  implicit val GetMyFollowedArtistsResponseArtistDecoder: Decoder[GetMyFollowedArtistsResponse.Artist] = deriveDecoder
  implicit val GetMyFollowedArtistsResponseArtistsDecoder: Decoder[GetMyFollowedArtistsResponse.Artists] = deriveDecoder
  implicit val GetMyFollowedArtistsResponseDecoder: Decoder[GetMyFollowedArtistsResponse] = deriveDecoder
}
