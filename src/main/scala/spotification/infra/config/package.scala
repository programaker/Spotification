package spotification.infra

import spotification.domain.config.{AppConfig, AuthorizationConfig, LogConfig, PlaylistConfig, ServerConfig}
import zio._

package object config {
  // Abstracts Config services dependencies, in case they change
  type ConfigServiceEnv = BaseEnv
  object ConfigServiceEnv {
    val layer: ULayer[BaseEnv] = BaseEnv.layer
  }

  type AuthorizationConfigModule = Has[AuthorizationConfig]
  object AuthorizationConfigModule {
    val config: RIO[AuthorizationConfigModule, AuthorizationConfig] = ZIO.access(_.get)
    val layer: RLayer[ConfigServiceEnv, AuthorizationConfigModule] = makeLayer(_.get.authorization)
  }

  type PlaylistConfigModule = Has[PlaylistConfig]
  object PlaylistConfigModule {
    val config: RIO[PlaylistConfigModule, PlaylistConfig] = ZIO.access(_.get)
    val layer: RLayer[ConfigServiceEnv, PlaylistConfigModule] = makeLayer(_.get.playlist)
  }

  type ServerConfigModule = Has[ServerConfig]
  object ServerConfigModule {
    val config: RIO[ServerConfigModule, ServerConfig] = ZIO.access(_.get)
    val layer: RLayer[ConfigServiceEnv, ServerConfigModule] = makeLayer(_.get.server)
  }

  type LogConfigModule = Has[LogConfig]
  object LogConfigModule {
    val config: RIO[LogConfigModule, LogConfig] = ZIO.access(_.get)
    val layer: RLayer[ConfigServiceEnv, LogConfigModule] = makeLayer(_.get.log)
  }

  private def makeLayer[A: Tagged](f: Has[AppConfig] => A): RLayer[BaseEnv, Has[A]] =
    appConfigLayer.map(f).map(Has(_))

  private val appConfigLayer: RLayer[ConfigServiceEnv, Has[AppConfig]] =
    ZLayer.fromEffect(PureConfigService.readConfig)
}
