package spotification.core.config

import zio.{Has, RIO, ZIO}

private[config] trait ConfigM {

  type SpotifyConfigModule = Has[SpotifyConfig]
  object SpotifyConfigModule {
    val spotifyConfig: RIO[SpotifyConfigModule, SpotifyConfig] = ZIO.access(_.get)
  }

  type ServerConfigModule = Has[ServerConfig]
  object ServerConfigModule {
    val serverConfig: RIO[ServerConfigModule, ServerConfig] = ZIO.access(_.get)
  }

}
