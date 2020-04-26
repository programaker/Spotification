package spotification.core.config

import zio.{Has, RIO, ZIO}

object ConfigModule {
  type SpotifyConfigService = Has[SpotifyConfig]
  object SpotifyConfigService {
    val spotifyConfig: RIO[SpotifyConfigService, SpotifyConfig] = ZIO.access(_.get)
  }

  type ServerConfigService = Has[ServerConfig]
  object ServerConfigService {
    val serverConfig: RIO[ServerConfigService, ServerConfig] = ZIO.access(_.get)
  }
}
