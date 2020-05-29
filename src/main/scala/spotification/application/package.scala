package spotification

import spotification.infra.config.{AuthorizationConfigModule, PlaylistConfigModule}
import spotification.infra.log.LogModule
import spotification.infra.spotify.authorization.AuthorizationModule
import spotification.infra.spotify.playlist.PlaylistModule
import zio.TaskLayer

package object application {
  type SpotifyAuthorizationAppEnv = AuthorizationModule with AuthorizationConfigModule
  object SpotifyAuthorizationAppEnv {
    val layer: TaskLayer[SpotifyAuthorizationAppEnv] =
      AuthorizationModule.layer ++ AuthorizationConfigModule.layer
  }

  type ReleaseRadarAppEnv = LogModule with PlaylistModule with PlaylistConfigModule with SpotifyAuthorizationAppEnv
  object ReleaseRadarAppEnv {
    val layer: TaskLayer[ReleaseRadarAppEnv] =
      LogModule.layer ++ PlaylistModule.layer ++ PlaylistConfigModule.layer ++ SpotifyAuthorizationAppEnv.layer
  }

  type MergePlaylistsEnv = LogModule with PlaylistModule with PlaylistConfigModule with SpotifyAuthorizationAppEnv
  object MergePlaylistsEnv {
    val layer: TaskLayer[MergePlaylistsEnv] =
      LogModule.layer ++ PlaylistModule.layer ++ PlaylistConfigModule.layer ++ SpotifyAuthorizationAppEnv.layer
  }
}
