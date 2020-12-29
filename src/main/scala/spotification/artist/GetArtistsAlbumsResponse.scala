package spotification.artist

import spotification.album.{AlbumId, ReleaseDatePrecision, ReleaseDateString}
import spotification.common.UriString

final case class GetArtistsAlbumsResponse(
  items: List[GetArtistsAlbumsResponse.Album],
  next: UriString
)
object GetArtistsAlbumsResponse {
  final case class Album(
    id: AlbumId,
    release_date: ReleaseDateString,
    release_date_precision: ReleaseDatePrecision
  )
}
