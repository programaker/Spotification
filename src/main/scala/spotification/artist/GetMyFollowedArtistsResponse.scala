package spotification.artist

import spotification.common.UriString

final case class GetMyFollowedArtistsResponse(
  artists: GetMyFollowedArtistsResponse.Artists,
  next: UriString
)
object GetMyFollowedArtistsResponse {
  final case class Artists(items: List[ArtistId])
}
