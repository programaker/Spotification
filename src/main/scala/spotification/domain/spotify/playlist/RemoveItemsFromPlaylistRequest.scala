package spotification.domain.spotify.playlist

import spotification.domain.spotify.authorization.AccessToken
import spotification.domain.spotify.track.TrackUri
import eu.timepit.refined._

final case class RemoveItemsFromPlaylistRequest(
  accessToken: AccessToken,
  playlistId: PlaylistId,
  body: RemoveItemsFromPlaylistRequest.Body
)
object RemoveItemsFromPlaylistRequest {
  final case class Body(tracks: PlaylistItemsToProcess[TrackToRemove])
  final case class TrackToRemove(uri: TrackUri)

  def make(
    accessToken: AccessToken,
    playlistId: PlaylistId,
    tracks: PlaylistItemsToProcess[TrackUri]
  ): RemoveItemsFromPlaylistRequest =
    RemoveItemsFromPlaylistRequest(
      accessToken = accessToken,
      playlistId = playlistId,
      body = RemoveItemsFromPlaylistRequest.Body(
        // Since `PlaylistItemsToProcess[TrackUri]` is already valid, we don't need to refine again
        // We know the result of `map` will respect the refinement
        tracks = refineV[PlaylistItemsToProcessR].unsafeFrom(tracks.value.map(TrackToRemove))
      )
    )
}
