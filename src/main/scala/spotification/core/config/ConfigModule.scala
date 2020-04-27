package spotification.core.config

import spotification.infra.config.PureConfigService
import zio._

object ConfigModule {
  type ConfigEnv = SpotifyConfigService with ServerConfigService with LogConfigService
  type SpotifyConfigService = Has[SpotifyConfig]
  type ServerConfigService = Has[ServerConfig]
  type LogConfigService = Has[LogConfig]

  val spotifyConfig: RIO[SpotifyConfigService, SpotifyConfig] = ZIO.access(_.get)
  val serverConfig: RIO[ServerConfigService, ServerConfig] = ZIO.access(_.get)
  val logConfig: RIO[LogConfigService, LogConfig] = ZIO.access(_.get)

  val layer: Layer[Throwable, ConfigEnv] = {
    val appConfigLayer = ZLayer.fromEffect(PureConfigService.readConfig)

    appConfigLayer.map(_.get.spotify).map(Has(_)) ++
      appConfigLayer.map(_.get.server).map(Has(_)) ++
      appConfigLayer.map(_.get.log).map(Has(_))
  }
}
