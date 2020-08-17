package spotification.application

import spotification.domain.spotify.playlist._
import spotification.infra.Infra.refineRIO
import spotification.infra.spotify.playlist.PlaylistModule
import zio.{RIO, ZIO}
import spotification.domain.spotify.playlist.GetPlaylistsItemsResponse.TrackResponse
import cats.data.NonEmptyList

object PlaylistCleanUp {
  def clearPlaylist(req: GetPlaylistsItemsRequest.FirstRequest): RIO[PlaylistModule, Unit] =
    // `(currentUri, _) => currentUri`:
    // since we are deleting tracks,
    // we should always stay in the first page
    PlaylistPagination.foreachPage(req)(deleteTracks(_, req))((currentUri, _) => currentUri)(_ *> _)

  private def deleteTracks(
    items: NonEmptyList[TrackResponse],
    req: GetPlaylistsItemsRequest.FirstRequest
  ): RIO[PlaylistModule, Unit] =
    ZIO.foreachPar_(
      items.toList
        .to(LazyList)
        .map(_.uri)
        .grouped(PlaylistItemsToProcess.MaxSize)
        .map(_.toVector)
        .map(refineRIO[PlaylistModule, PlaylistItemsToProcessR](_))
        .map(_.map(RemoveItemsFromPlaylistRequest.make(_, req.playlistId, req.accessToken)))
        .map(_.flatMap(PlaylistModule.removeItemsFromPlaylist))
        .to(Iterable)
    )(identity)
}
