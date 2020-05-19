package spotification

import spotification.infra.config.{AuthorizationConfigModule, PlaylistConfigModule}
import spotification.infra.spotify.album.AlbumModule
import spotification.infra.spotify.authorization.AuthorizationModule
import spotification.infra.spotify.playlist.PlaylistModule
import zio.TaskLayer

package object application {
  type SpotifyAuthorizationAppEnv = AuthorizationModule with AuthorizationConfigModule
  object SpotifyAuthorizationAppEnv {
    val layer: TaskLayer[SpotifyAuthorizationAppEnv] =
      AuthorizationModule.layer ++ AuthorizationConfigModule.layer
  }

  type ReleaseRadarAppEnv = PlaylistModule with PlaylistConfigModule with AlbumModule with SpotifyAuthorizationAppEnv
  object ReleaseRadarAppEnv {
    val layer: TaskLayer[ReleaseRadarAppEnv] =
      PlaylistModule.layer ++ PlaylistConfigModule.layer ++ AlbumModule.layer ++ SpotifyAuthorizationAppEnv.layer
  }
}
