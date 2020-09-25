package spotification.infra.spotify.playlist

import spotification.domain.spotify.playlist._
import zio.Task

trait PlaylistService {
  def getPlaylistsItems(req: GetPlaylistsItemsRequest): Task[GetPlaylistsItemsResponse]
  def addItemsToPlaylist(req: AddItemsToPlaylistRequest): Task[PlaylistSnapshotResponse]
  def removeItemsFromPlaylist(req: RemoveItemsFromPlaylistRequest): Task[PlaylistSnapshotResponse]
}
