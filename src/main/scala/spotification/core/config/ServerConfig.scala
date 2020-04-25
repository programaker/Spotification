package spotification.core.config

import spotification.core.{Host, PositiveInt}

final case class ServerConfig(
  host: Host,
  port: PositiveInt
)
