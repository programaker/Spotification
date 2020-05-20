package spotification.application

import spotification.domain.spotify.authorization.AccessToken
import spotification.domain.spotify.playlist.{
  AddItemsToPlaylistRequest,
  AddItemsToPlaylistResponse,
  PlaylistId,
  TrackUrisToAdd,
  TrackUrisToAddR
}
import spotification.domain.spotify.track.TrackUri
import spotification.infra.Infra.refineRIO
import spotification.infra.spotify.playlist.PlaylistModule
import zio.{RIO, ZIO}
import cats.implicits._

object TrackImport {
  def importTracks(
    accessToken: AccessToken,
    trackUris: List[TrackUri],
    destPlaylist: PlaylistId
  ): RIO[PlaylistModule, Unit] =
    ZIO.foreachPar_ {
      trackUris
        .to(LazyList)
        .grouped(TrackUrisToAdd.MaxSize)
        .map(_.toVector)
        .map(refineRIO[PlaylistModule, TrackUrisToAddR](_))
        .map(_.flatMap(importTrackChunk(accessToken, _, destPlaylist)))
        .to(Iterable)
    }(identity)

  private def importTrackChunk(
    accessToken: AccessToken,
    trackUris: TrackUrisToAdd,
    destPlaylist: PlaylistId
  ): RIO[PlaylistModule, Unit] = {
    val reqBody = AddItemsToPlaylistRequest.Body(trackUris)
    val req = AddItemsToPlaylistRequest(accessToken, destPlaylist, reqBody)

    PlaylistModule.addItemsToPlaylist(req).flatMap {
      case AddItemsToPlaylistResponse.Success(_) =>
        RIO.unit

      case AddItemsToPlaylistResponse.Error(status, message) =>
        RIO.fail(new Exception(show"Error in AddItemsToPlaylist: status=$status, message='$message'"))
    }
  }
}
