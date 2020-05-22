package spotification.domain.spotify.playlist

import spotification.domain.spotify.album.{AlbumId, AlbumType}
import spotification.domain.spotify.playlist.GetPlaylistsItemsResponse.Success.TrackResponse
import spotification.domain.spotify.track.TrackUri
import spotification.domain.{PositiveInt, UriString}

sealed abstract class GetPlaylistsItemsResponse extends Product with Serializable
object GetPlaylistsItemsResponse {
  final case class Success(
    items: List[TrackResponse],
    next: Option[UriString]
  ) extends GetPlaylistsItemsResponse
  object Success {
    final case class TrackResponse(uri: TrackUri, album: AlbumResponse)
    final case class AlbumResponse(id: AlbumId, album_type: AlbumType)
  }

  final case class Error(status: Int, message: String) extends GetPlaylistsItemsResponse
}
