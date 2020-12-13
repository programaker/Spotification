package spotification.user.service

import spotification.user.{CreatePlaylistRequest, CreatePlaylistResponse}
import zio.Task

trait UserService {
  def createPlaylist(req: CreatePlaylistRequest): Task[CreatePlaylistResponse]
}
