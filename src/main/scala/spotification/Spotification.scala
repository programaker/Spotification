package spotification

import spotification.infra.log.LogModule._
import spotification.presentation.HttpAppEnv
import spotification.presentation.Presentation.runHttpApp
import zio.{ZEnv, ZIO}

import scala.util.control.NonFatal

object Spotification extends zio.App {
  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] =
    runHttpApp
      .catchSome { case NonFatal(e) => error(">>> Error <<<", e) }
      .provideCustomLayer(HttpAppEnv.layer)
      .fold(_ => 1, _ => 0)
}
