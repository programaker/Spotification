package spotification.authorization

import spotification.authorization.httpclient.AuthorizationServiceLayer
import spotification.authorization.program.SpotifyAuthorizationEnv
import spotification.config.source.AuthorizationConfigLayer
import zio.TaskLayer

package object api {
  val SpotifyAuthorizationLayer: TaskLayer[SpotifyAuthorizationEnv] =
    AuthorizationServiceLayer ++ AuthorizationConfigLayer
}
