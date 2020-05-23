package spotification.domain.spotify.playlist

import eu.timepit.refined.auto._
import spotification.domain.spotify.album.AlbumType
import spotification.domain.spotify.playlist.GetPlaylistsItemsResponse.Success.TrackResponse
import spotification.domain.spotify.track.TrackUri
import spotification.domain.{FieldsToReturn, UriString}

sealed abstract class GetPlaylistsItemsResponse extends Product with Serializable
object GetPlaylistsItemsResponse {
  val Fields: FieldsToReturn = "next,items.track(uri,album(album_type))"

  final case class Success(
    items: List[TrackResponse],
    next: Option[UriString]
  ) extends GetPlaylistsItemsResponse
  object Success {
    final case class TrackResponse(uri: TrackUri, album: AlbumResponse)
    final case class AlbumResponse(album_type: AlbumType)
  }

  final case class Error(status: Int, message: String) extends GetPlaylistsItemsResponse
}
