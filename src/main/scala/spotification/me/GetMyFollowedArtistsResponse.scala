package spotification.me

import spotification.artist.ArtistId
import spotification.common.UriString

final case class GetMyFollowedArtistsResponse(artists: GetMyFollowedArtistsResponse.Artists)
object GetMyFollowedArtistsResponse {
  final case class Artists(items: List[Artist], next: UriString)
  final case class Artist(id: ArtistId)
}
