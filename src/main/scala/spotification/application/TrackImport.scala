package spotification.application

import spotification.domain.spotify.authorization.AccessToken
import spotification.domain.spotify.playlist._
import spotification.domain.spotify.track.TrackUri
import spotification.infra.Infra.refineRIO
import spotification.infra.spotify.playlist.PlaylistModule
import zio.{RIO, ZIO}

object TrackImport {
  def importTracks(
    trackUris: List[TrackUri],
    accessToken: AccessToken,
    destPlaylist: PlaylistId
  ): RIO[PlaylistModule, Unit] =
    ZIO.foreachPar_ {
      trackUris
        .to(LazyList)
        .grouped(PlaylistItemsToProcess.MaxSize)
        .map(_.toVector)
        .map(refineRIO[PlaylistModule, PlaylistItemsToProcessR](_))
        .map(_.flatMap(importTrackChunk(_, accessToken, destPlaylist)))
        .to(Iterable)
    }(identity)

  private def importTrackChunk(
    trackUris: PlaylistItemsToProcess[TrackUri],
    accessToken: AccessToken,
    destPlaylist: PlaylistId
  ): RIO[PlaylistModule, Unit] = {
    val reqBody = AddItemsToPlaylistRequest.Body(trackUris)
    val req = AddItemsToPlaylistRequest(accessToken, destPlaylist, reqBody)
    PlaylistModule.addItemsToPlaylist(req).map(_ => ())
  }
}
