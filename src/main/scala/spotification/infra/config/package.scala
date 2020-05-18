package spotification.infra

import pureconfig.ConfigSource
import spotification.domain.config._
import spotification.infra.config.Config
import zio._

//==========
// IntelliJ is complaining about:
// import pureconfig.generic.auto._
// import eu.timepit.refined.pureconfig._
// not being used, but without them it does not compile
//==========
import pureconfig.generic.auto._
import eu.timepit.refined.pureconfig._
import Config.Implicits._

package object config {
  type AuthorizationConfigModule = Has[AuthorizationConfig]
  object AuthorizationConfigModule {
    val config: RIO[AuthorizationConfigModule, AuthorizationConfig] = ZIO.access(_.get)
    val layer: TaskLayer[AuthorizationConfigModule] = makeLayer(_.get.authorization)
  }

  type PlaylistConfigModule = Has[PlaylistConfig]
  object PlaylistConfigModule {
    val config: RIO[PlaylistConfigModule, PlaylistConfig] = ZIO.access(_.get)
    val layer: TaskLayer[PlaylistConfigModule] = makeLayer(_.get.playlist)
  }

  type AlbumConfigModule = Has[AlbumConfig]
  object AlbumConfigModule {
    val config: RIO[AlbumConfigModule, AlbumConfig] = ZIO.access(_.get)
    val layer: TaskLayer[AlbumConfigModule] = makeLayer(_.get.album)
  }

  type ServerConfigModule = Has[ServerConfig]
  object ServerConfigModule {
    val config: RIO[ServerConfigModule, ServerConfig] = ZIO.access(_.get)
    val layer: TaskLayer[ServerConfigModule] = makeLayer(_.get.server)
  }

  type LogConfigModule = Has[LogConfig]
  object LogConfigModule {
    val config: RIO[LogConfigModule, LogConfig] = ZIO.access(_.get)
    val layer: TaskLayer[LogConfigModule] = makeLayer(_.get.log)
  }

  private def makeLayer[A: Tag](f: Has[AppConfig] => A): TaskLayer[Has[A]] =
    appConfigLayer.map(f).map(Has(_))

  private val appConfigLayer: TaskLayer[Has[AppConfig]] =
    ZLayer.fromEffect {
      IO.fromEither(ConfigSource.default.load[AppConfig])
        .mapError(_.prettyPrint())
        .absorbWith(new Exception(_))
    }
}
