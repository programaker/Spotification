package spotification.infra

import io.odin.{Logger, consoleLogger}
import zio.{Has, RIO, Task, TaskLayer, ZIO, ZLayer, ZManaged}
import zio.interop.catz._
import zio.interop.catz.implicits._
import io.odin.meta.Position
import io.odin.syntax._

package object log {
  type LogModule = Has[Logger[Task]]
  object LogModule {
    val live: TaskLayer[LogModule] = ZLayer.fromManaged {
      // Why not just combine the logs and call `toManaged`?
      // This way to construct the ZManaged provides all the needed implicits through the `ceff` argument
      ZManaged.fromEffect(Task.concurrentEffect).flatMap { implicit ceff =>
        consoleLogger[Task]().withAsync().toManaged
      }
    }
  }

  def debug(msg: String)(implicit position: Position): RIO[LogModule, Unit] =
    ZIO.accessM(_.get.debug(msg))

  def info(msg: String)(implicit position: Position): RIO[LogModule, Unit] =
    ZIO.accessM(_.get.info(msg))

  def warn(msg: String)(implicit position: Position): RIO[LogModule, Unit] =
    ZIO.accessM(_.get.warn(msg))

  def error(msg: String)(implicit position: Position): RIO[LogModule, Unit] =
    ZIO.accessM(_.get.error(msg))

  def error(msg: String, e: Throwable)(implicit position: Position): RIO[LogModule, Unit] =
    ZIO.accessM(_.get.error(msg, e))
}
