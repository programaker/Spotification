package spotification.user

import zio.{Has, RIO, Task, ZIO}

package object service {
  type CreatePlaylistService = CreatePlaylistRequest => Task[CreatePlaylistResponse]
  type CreatePlaylistServiceR = Has[CreatePlaylistService]

  def createPlaylist(req: CreatePlaylistRequest): RIO[CreatePlaylistServiceR, CreatePlaylistResponse] =
    ZIO.accessM(_.get.apply(req))
}
