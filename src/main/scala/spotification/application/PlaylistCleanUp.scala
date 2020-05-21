package spotification.application

import spotification.domain.spotify.authorization.AccessToken
import spotification.domain.spotify.playlist.GetPlaylistsItemsRequest.FirstRequest
import spotification.domain.spotify.playlist.{GetPlaylistsItemsRequest, PlaylistId}
import spotification.infra.spotify.playlist.PlaylistModule
import zio.RIO

object PlaylistCleanUp {
  def clearPlaylist(playlistId: PlaylistId, accessToken: AccessToken): RIO[PlaylistModule, Unit] =
    RIO.unit
  //PlaylistModule.getPlaylistItems(GetPlaylistsItemsRequest())
}
