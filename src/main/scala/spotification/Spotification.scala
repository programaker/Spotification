package spotification

import spotification.infra.log.LogModule._
import spotification.presentation._
import zio.{ExitCode, ZEnv, ZIO}

import scala.util.control.NonFatal

object Spotification extends zio.App {
  override def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] =
    runHttpApp
      .catchSome { case NonFatal(e) => error(">>> Error <<<", e) }
      .provideCustomLayer(HttpAppEnv.layer)
      .exitCode
}
