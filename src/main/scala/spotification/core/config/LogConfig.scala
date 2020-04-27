package spotification.core.config

import scala.concurrent.duration.FiniteDuration

final case class LogConfig(
  logDir: Directory,
  maxFileSizeInBytes: Option[Bytes],
  rolloverInterval: Option[FiniteDuration]
)
