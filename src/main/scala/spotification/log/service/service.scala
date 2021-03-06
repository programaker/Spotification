package spotification.log

import io.odin.Logger
import io.odin.meta.Position
import zio._

package object service {
  type LogR = Has[Logger[Task]]

  def debug(msg: String)(implicit position: Position): RIO[LogR, Unit] =
    ZIO.accessM(_.get.debug(msg))

  def info(msg: String)(implicit position: Position): RIO[LogR, Unit] =
    ZIO.accessM(_.get.info(msg))

  def warn(msg: String)(implicit position: Position): RIO[LogR, Unit] =
    ZIO.accessM(_.get.warn(msg))

  def error(msg: String)(implicit position: Position): RIO[LogR, Unit] =
    ZIO.accessM(_.get.error(msg))

  def error(msg: String, e: Throwable)(implicit position: Position): RIO[LogR, Unit] =
    ZIO.accessM(_.get.error(msg, e))
}
