package spotification.artist

import spotification.common.UriString

final case class GetCurrentUsersFollowedArtistsResponse(
  artists: GetCurrentUsersFollowedArtistsResponse.Artists,
  next: UriString
)
object GetCurrentUsersFollowedArtistsResponse {
  final case class Artists(items: List[ArtistId])
}
