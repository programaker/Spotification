package spotification

import cats.implicits._
import spotification.common.infra.concurrent.{ExecutionContextEnv, ExecutionContextLayer, executionContext}
import spotification.common.infra.httpserver.{addCors, addLogger, httpApp, runHttpServer}
import spotification.common.api.{ApiEnv, ApiLayer, allRoutes}
import spotification.config.application.{ServerConfigEnv, serverConfig}
import spotification.config.infra.ServerConfigLayer
import spotification.log.application.error
import zio.clock.Clock
import zio.interop.catz._
import zio._

import scala.util.control.NonFatal

object SpotificationHttpApp extends zio.App {
  type HttpAppEnv = ServerConfigEnv with ExecutionContextEnv with ApiEnv with Clock

  val HttpAppLayer: TaskLayer[HttpAppEnv] =
    ServerConfigLayer ++ ExecutionContextLayer ++ ApiLayer ++ Clock.live

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
