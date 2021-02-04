package spotification.playlist

import eu.timepit.refined.auto._
import spotification.album.AlbumType
import spotification.common.{CurrentUri, FieldsToReturn, NextUri}
import spotification.playlist.GetPlaylistsItemsResponse.TrackResponse
import spotification.track.TrackUri

final case class GetPlaylistsItemsResponse(
  items: List[GetPlaylistsItemsResponse.ItemResponse],
  href: CurrentUri,
  next: Option[NextUri]
) {
  def tracks: List[TrackResponse] = items.flatMap(_.track)
}
object GetPlaylistsItemsResponse {
  // Changes in this String will cause changes in the response structure
  // That's why they are together
  val Fields: FieldsToReturn = "items.track(uri,album(album_type)),href,next"

  final case class ItemResponse(track: Option[TrackResponse]) //`"track": null` = track unavailable
  final case class TrackResponse(album: AlbumResponse, uri: TrackUri)
  final case class AlbumResponse(album_type: AlbumType)
}
