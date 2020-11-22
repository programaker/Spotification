package spotification.playlist.service

import spotification.playlist._
import zio.Task

trait PlaylistService {
  def getPlaylistsItems(req: GetPlaylistsItemsRequest): Task[GetPlaylistsItemsResponse]
  def addItemsToPlaylist(req: AddItemsToPlaylistRequest): Task[PlaylistSnapshotResponse]
  def removeItemsFromPlaylist(req: RemoveItemsFromPlaylistRequest): Task[PlaylistSnapshotResponse]
  def createPlaylist(req: CreatePlaylistRequest): Task[CreatePlaylistResponse]
}
