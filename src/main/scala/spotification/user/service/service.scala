package spotification.user

import zio.{Has, RIO, ZIO}

package object service {
  type UserServiceEnv = Has[UserService]

  def createPlaylist(req: CreatePlaylistRequest): RIO[UserServiceEnv, CreatePlaylistResponse] =
    ZIO.accessM(_.get.createPlaylist(req))
}
