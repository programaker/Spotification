package spotification.config

import pureconfig.ConfigSource
import spotification.config.service.{
  AuthorizationConfigEnv,
  ClientConfigEnv,
  ConcurrentConfigEnv,
  MeConfigEnv,
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
  val AuthorizationConfigLayer: TaskLayer[AuthorizationConfigEnv] = makeLayer(_.authorization)
  val PlaylistConfigLayer: TaskLayer[PlaylistConfigEnv] = makeLayer(_.playlist)
  val TrackConfigLayer: TaskLayer[TrackConfigEnv] = makeLayer(_.track)
  val MeConfigLayer: TaskLayer[MeConfigEnv] = makeLayer(_.me)
  val UserConfigLayer: TaskLayer[UserConfigEnv] = makeLayer(_.user)
  val ServerConfigLayer: TaskLayer[ServerConfigEnv] = makeLayer(_.server)
  val ClientConfigLayer: TaskLayer[ClientConfigEnv] = makeLayer(_.client)
  val ConcurrentConfigLayer: TaskLayer[ConcurrentConfigEnv] = makeLayer(_.concurrent)

  private def makeLayer[A: Tag](f: AppConfig => A): TaskLayer[Has[A]] =
    appConfigLayer.map(_.get).map(f).map(Has(_))

  private lazy val appConfigLayer: TaskLayer[Has[AppConfig]] =
    ZLayer.fromEffect {
      IO.fromEither(ConfigSource.default.load[AppConfig])
        .mapError(_.prettyPrint())
        .absorbWith(new Exception(_))
    }
}
