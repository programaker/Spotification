package spotification.playlist

import spotification.effect.accessServiceFunction
import zio.{Has, RIO, Task}

package object service {
  type GetPlaylistItemsService = GetPlaylistsItemsRequest[_] => Task[GetPlaylistsItemsResponse]
  type GetPlaylistItemsServiceR = Has[GetPlaylistItemsService]
  def getPlaylistItems(req: GetPlaylistsItemsRequest[_]): RIO[GetPlaylistItemsServiceR, GetPlaylistsItemsResponse] =
    accessServiceFunction(req)

  type AddItemsToPlaylistService = AddItemsToPlaylistRequest => Task[PlaylistSnapshotResponse]
  type AddItemsToPlaylistServiceR = Has[AddItemsToPlaylistService]
  def addItemsToPlaylist(req: AddItemsToPlaylistRequest): RIO[AddItemsToPlaylistServiceR, PlaylistSnapshotResponse] =
    accessServiceFunction(req)

  type RemoveItemsFromPlaylistService = RemoveItemsFromPlaylistRequest => Task[PlaylistSnapshotResponse]
  type RemoveItemsFromPlaylistServiceR = Has[RemoveItemsFromPlaylistService]
  def removeItemsFromPlaylist(
    req: RemoveItemsFromPlaylistRequest
  ): RIO[RemoveItemsFromPlaylistServiceR, PlaylistSnapshotResponse] =
    accessServiceFunction(req)

  type CreatePlaylistService = CreatePlaylistRequest => Task[CreatePlaylistResponse]
  type CreatePlaylistServiceR = Has[CreatePlaylistService]
  def createPlaylist(req: CreatePlaylistRequest): RIO[CreatePlaylistServiceR, CreatePlaylistResponse] =
    accessServiceFunction(req)
}
