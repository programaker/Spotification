package spotification.playlist

import spotification.effect.accessServiceFunction
import zio.{Has, RIO, Task}

package object service {
  type GetPlaylistItemsService = GetPlaylistsItemsRequest[_] => Task[GetPlaylistsItemsResponse]
  type GetPlaylistItemsServiceR = Has[GetPlaylistItemsService]

  type AddItemsToPlaylistService = AddItemsToPlaylistRequest => Task[PlaylistSnapshotResponse]
  type AddItemsToPlaylistServiceR = Has[AddItemsToPlaylistService]

  type RemoveItemsFromPlaylistService = RemoveItemsFromPlaylistRequest => Task[PlaylistSnapshotResponse]
  type RemoveItemsFromPlaylistServiceR = Has[RemoveItemsFromPlaylistService]

  type CreatePlaylistService = CreatePlaylistRequest => Task[CreatePlaylistResponse]
  type CreatePlaylistServiceR = Has[CreatePlaylistService]

  def getPlaylistItems(req: GetPlaylistsItemsRequest[_]): RIO[GetPlaylistItemsServiceR, GetPlaylistsItemsResponse] =
    accessServiceFunction(req)

  def addItemsToPlaylist(req: AddItemsToPlaylistRequest): RIO[AddItemsToPlaylistServiceR, PlaylistSnapshotResponse] =
    accessServiceFunction(req)

  def removeItemsFromPlaylist(
    req: RemoveItemsFromPlaylistRequest
  ): RIO[RemoveItemsFromPlaylistServiceR, PlaylistSnapshotResponse] =
    accessServiceFunction(req)

  def createPlaylist(req: CreatePlaylistRequest): RIO[CreatePlaylistServiceR, CreatePlaylistResponse] =
    accessServiceFunction(req)
}
