package spotification

import spotification.infra.config.AuthorizationConfigModule
import spotification.infra.spotify.authorization.AuthorizationModule
import spotification.infra.{BaseEnv, ConfigServiceAndHttpClientEnv}
import zio.RLayer

package object application {
  type SpotifyAuthorizationEnv = AuthorizationModule with AuthorizationConfigModule with BaseEnv
  object SpotifyAuthorizationEnv {
    val layer: RLayer[ConfigServiceAndHttpClientEnv, SpotifyAuthorizationEnv] =
      AuthorizationModule.layer ++ AuthorizationConfigModule.layer ++ BaseEnv.layer
  }
}
