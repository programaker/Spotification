package spotification.domain.spotify.playlist

import spotification.domain.spotify.authorization.AccessToken
import spotification.domain.spotify.playlist.AddItemsToPlaylistRequest.Body
import spotification.domain.spotify.track.TrackUri

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
