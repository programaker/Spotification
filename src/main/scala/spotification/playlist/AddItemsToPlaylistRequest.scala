package spotification.playlist

import spotification.authorization.AccessToken
import spotification.playlist.AddItemsToPlaylistRequest.Body
import spotification.track.TrackUri

final case class AddItemsToPlaylistRequest(
  accessToken: AccessToken,
  playlistId: PlaylistId,
  body: Body
)
object AddItemsToPlaylistRequest {
  final case class Body(uris: PlaylistItemsToProcess[TrackUri])

  def make(
    accessToken: AccessToken,
    playlistId: PlaylistId,
    uris: PlaylistItemsToProcess[TrackUri]
  ): AddItemsToPlaylistRequest =
    AddItemsToPlaylistRequest(accessToken, playlistId, Body(uris))
}
