package spotification

import spotification.concurrent.{ExecutionContextEnv, executionContext}
import spotification.common.infra.httpserver.{addCors, addLogger, httpApp, runHttpServer}
import spotification.api._
import spotification.config.application.{ServerConfigEnv, serverConfig}
import spotification.log.application.error
import zio.clock.Clock
import zio.interop.catz._
import zio._

import scala.util.control.NonFatal

object SpotificationHttpApp extends zio.App {
  type HttpAppEnv = ServerConfigEnv with ExecutionContextEnv with PresentationEnv with Clock
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
