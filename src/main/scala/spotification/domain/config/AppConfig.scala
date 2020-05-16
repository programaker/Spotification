package spotification.domain.config

final case class AppConfig(
  authorization: AuthorizationConfig,
  playlist: PlaylistConfig,
  server: ServerConfig,
  log: LogConfig
)
