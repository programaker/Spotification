package spotification.application

import spotification.domain.spotify.playlist._
import spotification.infra.Infra.refineRIO
import spotification.infra.spotify.playlist.PlaylistModule
import zio.{RIO, ZIO}
import spotification.domain.spotify.playlist.GetPlaylistsItemsResponse.Success.TrackResponse

object PlaylistCleanUp {
  def clearPlaylist(req: GetPlaylistsItemsRequest.FirstRequest): RIO[PlaylistModule, Unit] =
    // `(currentUri, _) => currentUri`: since we are deleting tracks, 
    // we should stay aways in the first page
    PlaylistPagination.foreachPage(req)(deleteTracks(_, req))((currentUri, _) => currentUri)(_ *> _)

  private def deleteTracks(
    items: List[TrackResponse],
    req: GetPlaylistsItemsRequest.FirstRequest
  ): RIO[PlaylistModule, Unit] =
    ZIO.foreachPar_(
      items
        .to(LazyList)
        .map(_.uri)
        .grouped(PlaylistItemsToProcess.MaxSize)
        .map(_.toVector)
        .map(refineRIO[PlaylistModule, PlaylistItemsToProcessR](_))
        .map(_.map(RemoveItemsFromPlaylistRequest.make(req.accessToken, req.playlistId, _)))
        .map(_.flatMap(PlaylistModule.removeItemsFromPlaylist))
        .to(Iterable)
    )(identity)
}
