package spotification.playlist

import zio._

package object service {
  type PlaylistServiceEnv = Has[PlaylistService]

  def getPlaylistItems(req: GetPlaylistsItemsRequest[_]): RIO[PlaylistServiceEnv, GetPlaylistsItemsResponse] =
    ZIO.accessM(_.get.getPlaylistsItems(req))

  def addItemsToPlaylist(req: AddItemsToPlaylistRequest): RIO[PlaylistServiceEnv, PlaylistSnapshotResponse] =
    ZIO.accessM(_.get.addItemsToPlaylist(req))

  def removeItemsFromPlaylist(req: RemoveItemsFromPlaylistRequest): RIO[PlaylistServiceEnv, PlaylistSnapshotResponse] =
    ZIO.accessM(_.get.removeItemsFromPlaylist(req))
}
