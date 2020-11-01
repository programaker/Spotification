package spotification.config

import pureconfig.ConfigSource
import spotification.config.application.{
  AuthorizationConfigEnv,
  ClientConfigEnv,
  PlaylistConfigEnv,
  ServerConfigEnv,
  TrackConfigEnv
}
import zio.{Has, IO, Tag, TaskLayer, ZLayer}

package object infra {
  val AuthorizationConfigLayer: TaskLayer[AuthorizationConfigEnv] = makeLayer(_.get.authorization)
  val PlaylistConfigLayer: TaskLayer[PlaylistConfigEnv] = makeLayer(_.get.playlist)
  val TrackConfigLayer: TaskLayer[TrackConfigEnv] = makeLayer(_.get.track)
  val ServerConfigLayer: TaskLayer[ServerConfigEnv] = makeLayer(_.get.server)
  val ClientConfigLayer: TaskLayer[ClientConfigEnv] = makeLayer(_.get.client)

  private def makeLayer[A: Tag](f: Has[AppConfig] => A): TaskLayer[Has[A]] =
    appConfigLayer.map(f).map(Has(_))

  private def appConfigLayer: TaskLayer[Has[AppConfig]] =
    ZLayer.fromEffect {
      IO.fromEither(ConfigSource.default.load[AppConfig])
        .mapError(_.prettyPrint())
        .absorbWith(new Exception(_))
    }
}
