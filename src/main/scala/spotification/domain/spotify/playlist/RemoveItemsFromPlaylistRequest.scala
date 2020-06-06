package spotification.domain.spotify.playlist

import spotification.domain.spotify.authorization.AccessToken
import spotification.domain.spotify.track.TrackUri
import eu.timepit.refined._

final case class RemoveItemsFromPlaylistRequest(
  body: RemoveItemsFromPlaylistRequest.Body,
  playlistId: PlaylistId,
  accessToken: AccessToken
)
object RemoveItemsFromPlaylistRequest {
  final case class Body(tracks: PlaylistItemsToProcess[TrackToRemove])
  final case class TrackToRemove(uri: TrackUri)

  def make(
    tracks: PlaylistItemsToProcess[TrackUri],
    playlistId: PlaylistId,
    accessToken: AccessToken
  ): RemoveItemsFromPlaylistRequest =
    RemoveItemsFromPlaylistRequest(
      Body(
        // Since `PlaylistItemsToProcess[TrackUri]` is already valid, we don't need to refine again
        // We know the result of `map` will respect the refinement
        refineV[PlaylistItemsToProcessR].unsafeFrom(tracks.value.map(TrackToRemove))
      ),
      playlistId,
      accessToken
    )
}
