package spotification.playlist

import zio._

package object service {
  type GetPlaylistItemsService = GetPlaylistsItemsRequest[_] => Task[GetPlaylistsItemsResponse]
  type GetPlaylistItemsServiceR = Has[GetPlaylistItemsService]

  type AddItemsToPlaylistService = AddItemsToPlaylistRequest => Task[PlaylistSnapshotResponse]
  type AddItemsToPlaylistServiceR = Has[AddItemsToPlaylistService]

  type RemoveItemsFromPlaylistService = RemoveItemsFromPlaylistRequest => Task[PlaylistSnapshotResponse]
  type RemoveItemsFromPlaylistServiceR = Has[RemoveItemsFromPlaylistService]

  def getPlaylistItems(req: GetPlaylistsItemsRequest[_]): RIO[GetPlaylistItemsServiceR, GetPlaylistsItemsResponse] =
    ZIO.accessM(_.get.apply(req))

  def addItemsToPlaylist(req: AddItemsToPlaylistRequest): RIO[AddItemsToPlaylistServiceR, PlaylistSnapshotResponse] =
    ZIO.accessM(_.get.apply(req))

  def removeItemsFromPlaylist(
    req: RemoveItemsFromPlaylistRequest
  ): RIO[RemoveItemsFromPlaylistServiceR, PlaylistSnapshotResponse] =
    ZIO.accessM(_.get.apply(req))
}
