package spotification.infra

import spotification.domain.config.{AppConfig, LogConfig, ServerConfig, SpotifyConfig}
import zio._

package object config {
  type SpotifyConfigModule = Has[SpotifyConfig]
  object SpotifyConfigModule {
    val config: RIO[SpotifyConfigModule, SpotifyConfig] = ZIO.access(_.get)
    val layer: TaskLayer[SpotifyConfigModule] = appConfigLayer.map(_.get.spotify).map(Has(_))
  }

  type ServerConfigModule = Has[ServerConfig]
  object ServerConfigModule {
    val config: RIO[ServerConfigModule, ServerConfig] = ZIO.access(_.get)
    val layer: TaskLayer[ServerConfigModule] = appConfigLayer.map(_.get.server).map(Has(_))
  }

  type LogConfigModule = Has[LogConfig]
  object LogConfigModule {
    val config: RIO[LogConfigModule, LogConfig] = ZIO.access(_.get)
    val layer: TaskLayer[LogConfigModule] = appConfigLayer.map(_.get.log).map(Has(_))
  }

  private val appConfigLayer: TaskLayer[Has[AppConfig]] = ZLayer.fromEffect(PureConfigService.readConfig)
}
