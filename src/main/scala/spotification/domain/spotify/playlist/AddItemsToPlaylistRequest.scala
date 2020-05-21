package spotification.domain.spotify.playlist

import spotification.domain.spotify.authorization.AccessToken
import spotification.domain.spotify.playlist.AddItemsToPlaylistRequest.Body
import spotification.domain.spotify.track.TrackUri

final case class AddItemsToPlaylistRequest(
  accessToken: AccessToken,
  playlistId: PlaylistId,
  body: Body
)
object AddItemsToPlaylistRequest {
  final case class Body(uris: PlaylistItemsToProcess[TrackUri])
}
