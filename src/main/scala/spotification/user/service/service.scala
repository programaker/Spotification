package spotification.user

import zio.{Has, RIO, Task, ZIO}

package object service {
  type CreatePlaylistService = CreatePlaylistRequest => Task[CreatePlaylistResponse]
  type CreatePlaylistServiceEnv = Has[CreatePlaylistService]

  def createPlaylist(req: CreatePlaylistRequest): RIO[CreatePlaylistServiceEnv, CreatePlaylistResponse] =
    ZIO.accessM(_.get.apply(req))
}
