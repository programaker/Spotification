package spotification.domain.config

final case class AppConfig(
  authorization: AuthorizationConfig,
  playlist: PlaylistConfig,
  userConfig: UserConfig,
  server: ServerConfig,
  log: LogConfig
)
