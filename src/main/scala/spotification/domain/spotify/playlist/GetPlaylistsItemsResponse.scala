package spotification.domain.spotify.playlist

import eu.timepit.refined.auto._
import spotification.domain.{CurrentUri, FieldsToReturn, NextUri}
import spotification.domain.spotify.album.AlbumType
import spotification.domain.spotify.track.TrackUri

final case class GetPlaylistsItemsResponse(
  items: List[GetPlaylistsItemsResponse.ItemResponse],
  href: CurrentUri,
  next: Option[NextUri]
)
object GetPlaylistsItemsResponse {
  val Fields: FieldsToReturn = "items.track(uri,album(album_type)),href,next"
  final case class ItemResponse(track: Option[TrackResponse]) //`"track": null` = track unavailable
  final case class TrackResponse(album: AlbumResponse, uri: TrackUri)
  final case class AlbumResponse(album_type: AlbumType)
}
