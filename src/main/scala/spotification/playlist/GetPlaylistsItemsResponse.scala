package spotification.playlist

import eu.timepit.refined.auto._
import spotification.album.AlbumType
import spotification.common.{FieldsToReturn, UriString}
import spotification.track.TrackUri

final case class GetPlaylistsItemsResponse(
  items: List[GetPlaylistsItemsResponse.ItemResponse],
  href: UriString,
  next: Option[UriString]
) {
  def tracks: List[GetPlaylistsItemsResponse.TrackResponse] = items.flatMap(_.track)
}
object GetPlaylistsItemsResponse {
  final case class ItemResponse(track: Option[TrackResponse]) // `"track": null` = track unavailable

  final case class TrackResponse(album: AlbumResponse, uri: TrackUri) {
    def album_type: AlbumType = album.album_type
  }

  final case class AlbumResponse(album_type: AlbumType)

  // Changes in this String will cause changes in the response structure
  // That's why they are together
  val Fields: FieldsToReturn = "items.track(uri,album(album_type)),href,next"
}
