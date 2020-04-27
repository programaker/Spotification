package spotification.infra.log

import cats.effect.Resource
import cats.kernel.Semigroup
import io.odin.config._
import io.odin.syntax._
import io.odin.{Logger, consoleLogger, rollingFileLogger}
import spotification.core.config.ConfigModule.LogConfigService
import spotification.core.config.LogConfig
import zio.interop.catz._
import zio.interop.catz.implicits._
import zio.{Has, Task, ZLayer, ZManaged}

object LogModule {
  type LogService = Has[Logger[Task]]

  val layer: ZLayer[LogConfigService, Throwable, LogService] =
    ZLayer.fromServiceManaged(ZManaged.fromFunctionM(makeLogger).provide)

  private def makeLogger(logConfig: LogConfig): ZManaged[Any, Throwable, Logger[Task]] =
    ZManaged.fromEffect(Task.concurrentEffect).flatMap { implicit ceff =>
      val consoleLog = consoleLogger[Task]().withAsync()

      val logDir = logConfig.logDir.value
      val rolloverInterval = logConfig.rolloverInterval
      val maxFileSize = logConfig.maxFileSizeInBytes.map(_.value)
      val fileNamePattern = file"$logDir/$year-$month-$day-$hour-$minute-$second.log"
      val fileLog = rollingFileLogger[Task](fileNamePattern, rolloverInterval, maxFileSize)

      // `consoleLog |+| fileLog` does not compile. It causes a
      // `value |+| is not a member of cats.effect.Resource` error.
      // Invoking the Semigroup directly works =S
      val combinedLog = Semigroup[Resource[Task, Logger[Task]]].combine(consoleLog, fileLog)
      combinedLog.toManaged
    }
}
