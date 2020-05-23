package spotification.application

import cats.data.NonEmptyList
import spotification.domain.spotify.authorization.AccessToken
import spotification.domain.spotify.playlist._
import spotification.domain.spotify.track.TrackUri
import spotification.infra.Infra.refineRIO
import spotification.infra.spotify.playlist.PlaylistModule
import zio.{RIO, ZIO}

object TrackImport {
  def importTracks(
    trackUris: NonEmptyList[TrackUri],
    accessToken: AccessToken,
    destPlaylist: PlaylistId
  ): RIO[PlaylistModule, Unit] =
    ZIO.foreachPar_ {
      trackUris.toList
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
  ): RIO[PlaylistModule, Unit] =
    PlaylistModule
      .addItemsToPlaylist(AddItemsToPlaylistRequest.make(accessToken, destPlaylist, trackUris))
      .map(_ => ())
}
