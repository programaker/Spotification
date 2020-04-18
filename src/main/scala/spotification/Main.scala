package spotification

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    SpotificationServer.stream[IO].compile.drain.as(ExitCode.Success)
}
