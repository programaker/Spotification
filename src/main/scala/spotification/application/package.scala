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

  type ReleaseRadarNoSinglesAppEnv = LogModule
    with PlaylistModule
    with PlaylistConfigModule
    with SpotifyAuthorizationAppEnv
  object ReleaseRadarNoSinglesAppEnv {
    val layer: TaskLayer[ReleaseRadarNoSinglesAppEnv] =
      LogModule.layer ++ PlaylistModule.layer ++ PlaylistConfigModule.layer ++ SpotifyAuthorizationAppEnv.layer
  }

  type MergedPlaylistsEnv = LogModule with PlaylistModule with PlaylistConfigModule with SpotifyAuthorizationAppEnv
  object MergedPlaylistsEnv {
    val layer: TaskLayer[MergedPlaylistsEnv] =
      LogModule.layer ++ PlaylistModule.layer ++ PlaylistConfigModule.layer ++ SpotifyAuthorizationAppEnv.layer
  }
}
