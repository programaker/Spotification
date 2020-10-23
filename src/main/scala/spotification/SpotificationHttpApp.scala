package spotification

import spotification.infra.concurrent.{ExecutionContextModule, executionContext}
import spotification.infra.config.{ServerConfigModule, serverConfig}
import spotification.infra.httpserver.{addCors, addLogger, httpApp, runHttpServer}
import spotification.infra.log._
import spotification.api._
import zio.clock.Clock
import zio.interop.catz._
import zio._

import scala.util.control.NonFatal

object SpotificationHttpApp extends zio.App {
  type HttpAppEnv = ServerConfigModule with ExecutionContextModule with PresentationEnv with Clock
  object HttpAppEnv {
    val live: TaskLayer[HttpAppEnv] =
      ServerConfigModule.live ++ ExecutionContextModule.live ++ PresentationEnv.live ++ Clock.live
  }

  val runHttpApp: RIO[HttpAppEnv, Unit] = ZIO.runtime[HttpAppEnv].flatMap { implicit rt =>
    for {
      config <- serverConfig
      ex     <- executionContext

      controllers = allRoutes[HttpAppEnv]
      app = addCors(addLogger(httpApp(controllers)))

      _ <- runHttpServer[RIO[HttpAppEnv, *]](config, app, ex)
    } yield ()
  }

  override def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] =
    runHttpApp
      .catchSome { case NonFatal(e) => error(">>> Error <<<", e) }
      .provideCustomLayer(HttpAppEnv.live)
      .exitCode
}
