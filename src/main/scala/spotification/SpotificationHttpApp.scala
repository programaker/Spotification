package spotification

import spotification.concurrent.{ExecutionContextEnv, ExecutionContextLayer, executionContext}
import spotification.httpserver.{addCors, addLogger, httpApp, runHttpServer}
import spotification.api.{ApiEnv, ApiEnvLayer, allRoutes}
import spotification.core.config.service.{ServerConfigEnv, serverConfig}
import spotification.config.ServerConfigLayer
import spotification.core.log.service.error
import zio.clock.Clock
import zio.interop.catz._
import zio._

import scala.util.control.NonFatal

object SpotificationHttpApp extends zio.App {
  type HttpAppEnv = ServerConfigEnv with ExecutionContextEnv with ApiEnv with Clock

  val HttpAppLayer: TaskLayer[HttpAppEnv] =
    ServerConfigLayer ++ ExecutionContextLayer ++ ApiEnvLayer ++ Clock.live

  def runHttpApp: RIO[HttpAppEnv, Unit] =
    ZIO.runtime[HttpAppEnv].flatMap { implicit rt =>
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
      .provideCustomLayer(HttpAppLayer)
      .exitCode
}
