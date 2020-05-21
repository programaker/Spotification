package spotification.domain.spotify.playlist

import spotification.domain.spotify.authorization.AccessToken
import spotification.domain.spotify.track.TrackUri

final case class RemoveItemsFromPlaylistRequest(
  accessToken: AccessToken,
  playlistId: PlaylistId,
  body: RemoveItemsFromPlaylistRequest.Body
)
object RemoveItemsFromPlaylistRequest {
  final case class Body(tracks: PlaylistItemsToProcess[TrackToRemove])
  final case class TrackToRemove(uri: TrackUri)
}
