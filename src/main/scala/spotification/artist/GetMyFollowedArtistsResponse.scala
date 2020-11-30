package spotification.artist

import spotification.artist.GetMyFollowedArtistsResponse.Artists
import spotification.common.UriString

final case class GetMyFollowedArtistsResponse(artists: Artists)
object GetMyFollowedArtistsResponse {
  final case class Artists(items: List[Artist], next: UriString)
  final case class Artist(id: ArtistId)
}
