package spotification

import org.http4s.HttpRoutes
import spotification.application.{MergedPlaylistsEnv, ReleaseRadarNoSinglesAppEnv, SpotifyAuthorizationAppEnv}
import spotification.infra.concurrent.ExecutionContextModule
import spotification.infra.config.ServerConfigModule
import zio.TaskLayer
import zio.clock.Clock

package object presentation {
  type RoutesMapping[F[_]] = (String, HttpRoutes[F])
  type Routes[F[_]] = Seq[RoutesMapping[F]]

  type PresentationEnv = SpotifyAuthorizationAppEnv with ReleaseRadarNoSinglesAppEnv with MergedPlaylistsEnv
  object PresentationEnv {
    val layer: TaskLayer[PresentationEnv] =
      SpotifyAuthorizationAppEnv.layer ++ ReleaseRadarNoSinglesAppEnv.layer ++ MergedPlaylistsEnv.layer
  }

  type HttpAppEnv = ServerConfigModule with ExecutionContextModule with PresentationEnv with Clock
  object HttpAppEnv {
    val layer: TaskLayer[HttpAppEnv] =
      ServerConfigModule.layer ++ ExecutionContextModule.layer ++ PresentationEnv.layer ++ Clock.live
  }
}
