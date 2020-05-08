package spotification.infra.config

import spotification.domain.config.{AppConfig, LogConfig, ServerConfig, SpotifyConfig}
import zio._

object ConfigZIO {
  type SpotifyConfigService = Has[SpotifyConfig]
  type ServerConfigService = Has[ServerConfig]
  type LogConfigService = Has[LogConfig]

  val spotifyConfig: RIO[SpotifyConfigService, SpotifyConfig] = ZIO.access(_.get)
  val serverConfig: RIO[ServerConfigService, ServerConfig] = ZIO.access(_.get)
  val logConfig: RIO[LogConfigService, LogConfig] = ZIO.access(_.get)

  private val appConfigLayer: TaskLayer[Has[AppConfig]] = ZLayer.fromEffect(PureConfigService.readConfig)

  val spotifyConfigLayer: TaskLayer[SpotifyConfigService] = appConfigLayer.map(_.get.spotify).map(Has(_))
  val serverConfigLayer: TaskLayer[ServerConfigService] = appConfigLayer.map(_.get.server).map(Has(_))
  val logConfigLayer: TaskLayer[LogConfigService] = appConfigLayer.map(_.get.log).map(Has(_))
}
