package spotification.domain.spotify.playlist

import spotification.domain.spotify.album.{AlbumId, AlbumType}
import spotification.domain.{PositiveInt, UriString}

sealed abstract class GetPlaylistsItemsResponse extends Product with Serializable
object GetPlaylistsItemsResponse {
  final case class Success(
    items: List[TrackResponse],
    total: PositiveInt,
    next: Option[UriString]
  ) extends GetPlaylistsItemsResponse

  final case class Error(
    status: Int,
    message: String
  ) extends GetPlaylistsItemsResponse

  final case class TrackResponse(album: AlbumResponse)
  final case class AlbumResponse(id: AlbumId, album_type: AlbumType)
}
