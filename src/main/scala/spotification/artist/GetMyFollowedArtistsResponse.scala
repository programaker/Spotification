package spotification.artist

import spotification.common.UriString

final case class GetMyFollowedArtistsResponse(artists: GetMyFollowedArtistsResponse.Artists) {
  def artistIds: List[ArtistId] = artists.items.map(_.id)
  def next: Option[UriString] = artists.next
}
object GetMyFollowedArtistsResponse {
  final case class Artists(items: List[Artist], next: Option[UriString])
  final case class Artist(id: ArtistId)
}
