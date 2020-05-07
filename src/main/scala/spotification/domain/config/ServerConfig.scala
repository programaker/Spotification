package spotification.domain.config

import spotification.domain.{Host, PositiveInt}

final case class ServerConfig(
  host: Host,
  port: PositiveInt
)
