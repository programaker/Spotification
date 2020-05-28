package spotification.infra

import cats.kernel.Semigroup
import io.odin.{Logger, consoleLogger, rollingFileLogger}
import spotification.domain.config.LogConfig
import spotification.infra.config.LogConfigModule
import zio.{Has, RIO, Task, TaskLayer, TaskManaged, ZIO, ZLayer, ZManaged}
import zio.interop.catz._
import zio.interop.catz.implicits._
import io.odin.config._
import io.odin.syntax._

package object log {
  type LogModule = Has[Logger[Task]]
  object LogModule {
    val layer: TaskLayer[LogModule] =
      LogConfigModule.layer >>> ZLayer.fromServiceManaged[LogConfig, Any, Throwable, Logger[Task]](makeLogger)

    val log: RIO[LogModule, Logger[Task]] = ZIO.access(_.get)

    private def makeLogger(logConfig: LogConfig): TaskManaged[Logger[Task]] =
      // Why not just combine the logs and call `toManaged`?
      // This way to construct the ZManaged provides all the needed implicits through the `ceff` argument
      ZManaged.fromEffect(Task.concurrentEffect).flatMap { implicit ceff =>
        val consoleLog = consoleLogger[Task]().withAsync()

        val fileLog = {
          val logDir = logConfig.logDir.value
          val rolloverInterval = logConfig.rolloverInterval
          val maxFileSize = logConfig.maxFileSizeInBytes.map(_.value)
          val fileNamePattern = file"$logDir/$year-$month-$day-$hour-$minute-$second.log"
          rollingFileLogger[Task](fileNamePattern, rolloverInterval, maxFileSize)
        }

        // `consoleLog |+| fileLog` does not compile. It causes a
        // `value |+| is not a member of cats.effect.Resource` error.
        // Invoking the Semigroup directly works =S
        val combinedLog = Semigroup.combine(consoleLog, fileLog)

        combinedLog.toManaged
      }
  }
}
