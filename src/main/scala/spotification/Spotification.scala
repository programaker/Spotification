package spotification
import spotification.application.Application
import zio.ZIO

object Spotification extends zio.App {
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] = Application.runApplication
}
