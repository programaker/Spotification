package spotification.domain.spotify.playlist

import spotification.domain.spotify.authorization.AccessToken
import spotification.domain.spotify.playlist.AddItemsToPlaylistRequest.Body

final case class AddItemsToPlaylistRequest(
  accessToken: AccessToken,
  playlistId: PlaylistId,
  body: Body
)
object AddItemsToPlaylistRequest {
  final case class Body(uris: TrackUrisToAdd)
}
