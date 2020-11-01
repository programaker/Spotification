package spotification.spotify.playlist

import spotification.spotify.authorization.AccessToken
import spotification.spotify.playlist.AddItemsToPlaylistRequest.Body
import spotification.track.TrackUri

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
