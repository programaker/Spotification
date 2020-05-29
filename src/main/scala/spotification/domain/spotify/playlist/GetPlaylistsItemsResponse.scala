package spotification.domain.spotify.playlist

import eu.timepit.refined.auto._
import spotification.domain.spotify.album.AlbumType
import spotification.domain.spotify.playlist.GetPlaylistsItemsResponse.Success.ItemResponse
import spotification.domain.spotify.track.TrackUri
import spotification.domain.CurrentUri
import spotification.domain.NextUri
import spotification.domain.FieldsToReturn

sealed abstract class GetPlaylistsItemsResponse extends Product with Serializable
object GetPlaylistsItemsResponse {
  val Fields: FieldsToReturn = "items.track(uri,album(album_type)),href,next"

  final case class Success(
    items: List[ItemResponse],
    href: CurrentUri,
    next: Option[NextUri]
  ) extends GetPlaylistsItemsResponse
  object Success {
    final case class ItemResponse(track: Option[TrackResponse]) //`track:null` shouldn't happen, but I saw myself
    final case class TrackResponse(album: AlbumResponse, uri: TrackUri)
    final case class AlbumResponse(album_type: AlbumType)
  }

  final case class Error(status: Int, message: String) extends GetPlaylistsItemsResponse
}
