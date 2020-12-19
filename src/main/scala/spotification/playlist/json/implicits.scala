package spotification.playlist.json

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.refined._
import io.estatico.newtype.ops.toCoercibleIdOps
import spotification.common.SpotifyId
import spotification.playlist.{
  AddItemsToPlaylistRequest,
  GetPlaylistsItemsResponse,
  MergePlaylistsRequest,
  PlaylistId,
  PlaylistSnapshotResponse,
  ReleaseRadarNoSinglesRequest,
  RemoveItemsFromPlaylistRequest
}

object implicits {
  implicit val PlaylistIdDecoder: Decoder[PlaylistId] =
    implicitly[Decoder[SpotifyId]].map(_.coerce[PlaylistId])

  implicit val GetPlaylistsItemsResponseAlbumDecoder: Decoder[GetPlaylistsItemsResponse.AlbumResponse] =
    deriveDecoder

  implicit val GetPlaylistsItemsResponseTrackDecoder: Decoder[GetPlaylistsItemsResponse.TrackResponse] =
    deriveDecoder

  implicit val GetPlaylistsItemsResponseItemDecoder: Decoder[GetPlaylistsItemsResponse.ItemResponse] =
    deriveDecoder

  implicit val GetPlaylistsItemsResponseDecoder: Decoder[GetPlaylistsItemsResponse] =
    deriveDecoder

  implicit val AddItemsToPlaylistRequestBodyEncoder: Encoder[AddItemsToPlaylistRequest.Body] =
    deriveEncoder

  implicit val PlaylistSnapshotResponseDecoder: Decoder[PlaylistSnapshotResponse] =
    deriveDecoder

  implicit val RemoveItemsFromPlaylistRequestTrackEncoder: Encoder[RemoveItemsFromPlaylistRequest.TrackToRemove] =
    deriveEncoder

  implicit val RemoveItemsFromPlaylistRequestEncoder: Encoder[RemoveItemsFromPlaylistRequest.Body] =
    deriveEncoder

  implicit val ReleaseRadarNoSinglesRequestDecoder: Decoder[ReleaseRadarNoSinglesRequest] =
    deriveDecoder

  implicit val MergePlaylistsRequestDecoder: Decoder[MergePlaylistsRequest] =
    deriveDecoder
}
