package spotification.domain.config

final case class AppConfig(
  spotify: SpotifyConfig,
  server: ServerConfig,
  log: LogConfig
)
