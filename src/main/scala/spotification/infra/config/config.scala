package spotification.infra

import pureconfig.ConfigSource
import spotification.domain.config._
import zio._
import spotification.infra.config.implicits._
import pureconfig.generic.auto._
import eu.timepit.refined.pureconfig._

package object config {
  type AuthorizationConfigModule = Has[AuthorizationConfig]
  object AuthorizationConfigModule {
    val live: TaskLayer[AuthorizationConfigModule] = makeLayer(_.get.authorization)
  }

  type PlaylistConfigModule = Has[PlaylistConfig]
  object PlaylistConfigModule {
    val live: TaskLayer[PlaylistConfigModule] = makeLayer(_.get.playlist)
  }

  type TrackConfigModule = Has[TrackConfig]
  object TrackConfigModule {
    val live: TaskLayer[TrackConfigModule] = makeLayer(_.get.track)
  }

  type ServerConfigModule = Has[ServerConfig]
  object ServerConfigModule {
    val live: TaskLayer[ServerConfigModule] = makeLayer(_.get.server)
  }

  type ClientConfigModule = Has[ClientConfig]
  object ClientConfigModule {
    val live: TaskLayer[ClientConfigModule] = makeLayer(_.get.client)
  }

  val authorizationConfig: RIO[AuthorizationConfigModule, AuthorizationConfig] =
    ZIO.access(_.get)

  val playlistConfig: RIO[PlaylistConfigModule, PlaylistConfig] =
    ZIO.access(_.get)

  val trackConfig: RIO[TrackConfigModule, TrackConfig] =
    ZIO.access(_.get)

  val serverConfig: RIO[ServerConfigModule, ServerConfig] =
    ZIO.access(_.get)

  val clientConfig: RIO[ClientConfigModule, ClientConfig] =
    ZIO.access(_.get)

  private def makeLayer[A: Tag](f: Has[AppConfig] => A): TaskLayer[Has[A]] =
    appConfigLayer.map(f).map(Has(_))

  private val appConfigLayer: TaskLayer[Has[AppConfig]] =
    ZLayer.fromEffect {
      IO.fromEither(ConfigSource.default.load[AppConfig])
        .mapError(_.prettyPrint())
        .absorbWith(new Exception(_))
    }
}
