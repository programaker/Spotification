package spotification.config

import pureconfig.ConfigSource
import spotification.config.service.{
  AuthorizationConfigEnv,
  ClientConfigEnv,
  ConcurrentConfigEnv,
  PlaylistConfigEnv,
  ServerConfigEnv,
  TrackConfigEnv,
  UserConfigEnv
}
import zio.{Has, IO, Tag, TaskLayer, ZLayer}
import pureconfig.generic.auto._
import eu.timepit.refined.pureconfig._
import spotification.config.implicits._

package object source {
  val AuthorizationConfigLayer: TaskLayer[AuthorizationConfigEnv] = makeLayer(_.get.authorization)
  val PlaylistConfigLayer: TaskLayer[PlaylistConfigEnv] = makeLayer(_.get.playlist)
  val TrackConfigLayer: TaskLayer[TrackConfigEnv] = makeLayer(_.get.track)
  val UserConfigLayer: TaskLayer[UserConfigEnv] = makeLayer(_.get.user)
  val ServerConfigLayer: TaskLayer[ServerConfigEnv] = makeLayer(_.get.server)
  val ClientConfigLayer: TaskLayer[ClientConfigEnv] = makeLayer(_.get.client)
  val ConcurrentConfigLayer: TaskLayer[ConcurrentConfigEnv] = makeLayer(_.get.concurrent)

  private def makeLayer[A: Tag](f: Has[AppConfig] => A): TaskLayer[Has[A]] =
    appConfigLayer.map(f).map(Has(_))

  private def appConfigLayer: TaskLayer[Has[AppConfig]] =
    ZLayer.fromEffect {
      IO.fromEither(ConfigSource.default.load[AppConfig])
        .mapError(_.prettyPrint())
        .absorbWith(new Exception(_))
    }
}
