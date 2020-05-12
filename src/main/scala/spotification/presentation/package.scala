package spotification

import org.http4s.HttpRoutes
import spotification.application.SpotifyAuthorizationAppEnv
import spotification.infra.concurrent.ExecutionContextModule
import spotification.infra.config.ServerConfigModule
import zio.TaskLayer

package object presentation {
  type RoutesMapping[F[_]] = (String, HttpRoutes[F])
  type Routes[F[_]] = Seq[RoutesMapping[F]]

  type PresentationEnv = SpotifyAuthorizationAppEnv
  object PresentationEnv {
    val layer: TaskLayer[PresentationEnv] = SpotifyAuthorizationAppEnv.layer
  }

  type HttpAppEnv = ServerConfigModule with ExecutionContextModule with PresentationEnv
  object HttpAppEnv {
    val layer: TaskLayer[HttpAppEnv] =
      ServerConfigModule.layer ++ ExecutionContextModule.layer ++ PresentationEnv.layer
  }
}
