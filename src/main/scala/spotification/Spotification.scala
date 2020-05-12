package spotification

import spotification.presentation.HttpAppEnv
import spotification.presentation.Presentation.runHttpApp
import zio.{ZEnv, ZIO}

object Spotification extends zio.App {
  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] =
    runHttpApp.provideCustomLayer(HttpAppEnv.layer).fold(_ => 1, _ => 0)
}
