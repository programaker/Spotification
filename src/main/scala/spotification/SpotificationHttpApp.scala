package spotification

import spotification.common.api.{ApiEnv, ApiLayer, allRoutes}
import spotification.common.httpclient.HttpClientLayer
import spotification.concurrent.{ExecutionContextEnv, ExecutionContextLayer, executionContext}
import spotification.config.service.{ServerConfigEnv, serverConfig}
import spotification.config.source._
import spotification.httpserver.{addCors, addLogger, httpApp, runHttpServer}
import spotification.log.service.error
import zio._
import zio.clock.Clock
import zio.interop.catz._

import scala.util.control.NonFatal

object SpotificationHttpApp extends zio.App {
  type HttpAppEnv = ServerConfigEnv with ExecutionContextEnv with ApiEnv with Clock
  val HttpAppLayer: TaskLayer[HttpAppEnv] =
    ServerConfigLayer >+>
      ConcurrentConfigLayer >+>
      ExecutionContextLayer >+>
      ClientConfigLayer >+>
      HttpClientLayer >+>
      AuthorizationConfigLayer >+>
      PlaylistConfigLayer >+>
      TrackConfigLayer >+>
      ApiLayer >+>
      Clock.live

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
