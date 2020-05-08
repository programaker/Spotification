package spotification

import spotification.infra.BaseModule
import spotification.infra.config.SpotifyConfigModule
import spotification.infra.spotify.authorization.AuthorizationModule

package object application {
  type SpotifyAuthorizationEnv = AuthorizationModule with SpotifyConfigModule with BaseModule
}
