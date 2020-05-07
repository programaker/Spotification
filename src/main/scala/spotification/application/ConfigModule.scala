package spotification.application

import spotification.domain.config.{LogConfig, ServerConfig, SpotifyConfig}
import spotification.infra.config.PureConfigService
import zio._

object ConfigModule {
  type ConfigServices = SpotifyConfigService with ServerConfigService with LogConfigService

  type SpotifyConfigService = Has[SpotifyConfig]
  type ServerConfigService = Has[ServerConfig]
  type LogConfigService = Has[LogConfig]

  def spotifyConfig: RIO[SpotifyConfigService, SpotifyConfig] = ZIO.access(_.get)
  def serverConfig: RIO[ServerConfigService, ServerConfig] = ZIO.access(_.get)
  def logConfig: RIO[LogConfigService, LogConfig] = ZIO.access(_.get)

  def layer: Layer[Throwable, ConfigServices] = {
    val appConfigLayer = ZLayer.fromEffect(PureConfigService.readConfig)

    appConfigLayer.map(_.get.spotify).map(Has(_)) ++
      appConfigLayer.map(_.get.server).map(Has(_)) ++
      appConfigLayer.map(_.get.log).map(Has(_))
  }
}
