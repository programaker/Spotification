package spotification.core.playlist

import spotification.core.authorization.AccessToken
import spotification.core.playlist.AddItemsToPlaylistRequest.Body
import spotification.core.track.TrackUri

final case class AddItemsToPlaylistRequest(
  playlistId: PlaylistId,
  body: Body,
  accessToken: AccessToken
)
object AddItemsToPlaylistRequest {
  final case class Body(uris: PlaylistItemsToProcess[TrackUri])

  def make(
    playlistId: PlaylistId,
    uris: PlaylistItemsToProcess[TrackUri],
    accessToken: AccessToken
  ): AddItemsToPlaylistRequest =
    AddItemsToPlaylistRequest(playlistId, Body(uris), accessToken)
}
