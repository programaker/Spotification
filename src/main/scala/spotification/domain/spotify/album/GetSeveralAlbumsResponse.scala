package spotification.domain.spotify.album

import spotification.domain.spotify.album.GetSeveralAlbumsResponse.Success.AlbumResponse
import spotification.domain.spotify.track.TrackUri

sealed abstract class GetSeveralAlbumsResponse extends Product with Serializable
object GetSeveralAlbumsResponse {
  final case class Success(albums: List[AlbumResponse]) extends GetSeveralAlbumsResponse
  object Success {
    final case class AlbumResponse(tracks: TracksResponse)
    final case class TracksResponse(items: List[ItemResponse])
    final case class ItemResponse(uri: TrackUri)
  }

  final case class Error(status: Int, message: String) extends GetSeveralAlbumsResponse
}
