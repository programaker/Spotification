package spotification

import spotification.infra.config.SpotifyConfigModule
import spotification.infra.spotify.authorization.AuthorizationModule
import spotification.infra.{BaseEnv, ConfigServiceAndHttpClientEnv}
import zio.RLayer

package object application {
  type SpotifyAuthorizationEnv = AuthorizationModule with SpotifyConfigModule with BaseEnv
  object SpotifyAuthorizationEnv {
    val layer: RLayer[ConfigServiceAndHttpClientEnv, SpotifyAuthorizationEnv] =
      AuthorizationModule.layer ++ SpotifyConfigModule.layer ++ BaseEnv.layer
  }
}
