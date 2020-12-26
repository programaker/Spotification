package spotification.playlist

import zio._

package object service {
  type GetPlaylistItemsService = GetPlaylistsItemsRequest[_] => Task[GetPlaylistsItemsResponse]
  type GetPlaylistItemsServiceEnv = Has[GetPlaylistItemsService]

  type AddItemsToPlaylistService = AddItemsToPlaylistRequest => Task[PlaylistSnapshotResponse]
  type AddItemsToPlaylistServiceEnv = Has[AddItemsToPlaylistService]

  type RemoveItemsFromPlaylistService = RemoveItemsFromPlaylistRequest => Task[PlaylistSnapshotResponse]
  type RemoveItemsFromPlaylistServiceEnv = Has[RemoveItemsFromPlaylistService]

  def getPlaylistItems(req: GetPlaylistsItemsRequest[_]): RIO[GetPlaylistItemsServiceEnv, GetPlaylistsItemsResponse] =
    ZIO.accessM(_.get.apply(req))

  def addItemsToPlaylist(req: AddItemsToPlaylistRequest): RIO[AddItemsToPlaylistServiceEnv, PlaylistSnapshotResponse] =
    ZIO.accessM(_.get.apply(req))

  def removeItemsFromPlaylist(
    req: RemoveItemsFromPlaylistRequest
  ): RIO[RemoveItemsFromPlaylistServiceEnv, PlaylistSnapshotResponse] =
    ZIO.accessM(_.get.apply(req))
}
