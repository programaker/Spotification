package spotification

import spotification.infra.concurrent.ExecutionContextModule
import spotification.infra.config.ServerConfigModule
import spotification.infra.httpserver.{addCors, addLogger, httpApp, runHttpServer}
import spotification.infra.log.LogModule._
import spotification.presentation._
import zio.clock.Clock
import zio.interop.catz._
import zio._

import scala.util.control.NonFatal

object SpotificationHttpApp extends zio.App {
  type HttpAppEnv = ServerConfigModule with ExecutionContextModule with PresentationEnv with Clock
  object HttpAppEnv {
    val layer: TaskLayer[HttpAppEnv] =
      ServerConfigModule.layer ++ ExecutionContextModule.layer ++ PresentationEnv.layer ++ Clock.live
  }

  val runHttpApp: RIO[HttpAppEnv, Unit] = ZIO.runtime[HttpAppEnv].flatMap { implicit rt =>
    for {
      config <- ServerConfigModule.config
      ex     <- ExecutionContextModule.executionContext

      controllers = allRoutes[HttpAppEnv]
      app = addCors(addLogger(httpApp(controllers)))

      _ <- runHttpServer[RIO[HttpAppEnv, *]](config, app, ex)
    } yield ()
  }

  override def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] =
    runHttpApp
      .catchSome { case NonFatal(e) => error(">>> Error <<<", e) }
      .provideCustomLayer(HttpAppEnv.layer)
      .exitCode
}
