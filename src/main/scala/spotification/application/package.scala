package spotification

import spotification.infra.config.AuthorizationConfigModule
import spotification.infra.spotify.authorization.AuthorizationModule
import zio.TaskLayer

package object application {
  type SpotifyAuthorizationAppEnv = AuthorizationModule with AuthorizationConfigModule
  object SpotifyAuthorizationAppEnv {
    val layer: TaskLayer[SpotifyAuthorizationAppEnv] =
      AuthorizationModule.layer ++ AuthorizationConfigModule.layer
  }
}