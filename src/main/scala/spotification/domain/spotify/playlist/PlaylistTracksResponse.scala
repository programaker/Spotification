package spotification.domain.spotify.playlist

import spotification.domain.{PositiveInt, UriString}
import spotification.domain.spotify.album.{AlbumId, AlbumType}
import spotification.domain.spotify.playlist.PlaylistTracksResponse.TrackResponse

final case class PlaylistTracksResponse(
  items: List[TrackResponse],
  total: PositiveInt,
  next: Option[UriString]
)
object PlaylistTracksResponse {
  final case class TrackResponse(album: AlbumResponse)
  final case class AlbumResponse(id: AlbumId, album_type: AlbumType)
}
