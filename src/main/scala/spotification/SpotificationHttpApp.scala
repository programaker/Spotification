package spotification

import spotification.common.api.{AllProgramsLayer, makeAllApis}
import spotification.common.httpclient.HttpClientLayer
import spotification.common.program.AllProgramsEnv
import spotification.concurrent.{ExecutionContextEnv, ExecutionContextLayer, executionContext}
import spotification.config.service.{ServerConfigEnv, serverConfig}
import spotification.config.source._
import spotification.httpserver.{addCors, addLogger, makeHttpApp, runHttpServer}
import spotification.log.service.error
import zio._
import zio.clock.Clock
import zio.interop.catz._

import scala.util.control.NonFatal

object SpotificationHttpApp extends zio.App {
  type HttpAppEnv = ServerConfigEnv with ExecutionContextEnv with AllProgramsEnv with Clock

  val HttpAppLayer: TaskLayer[HttpAppEnv] =
    ServerConfigLayer >+>
      ConcurrentConfigLayer >+>
      ExecutionContextLayer >+>
      ClientConfigLayer >+>
      HttpClientLayer >+>
      AuthorizationConfigLayer >+>
      PlaylistConfigLayer >+>
      TrackConfigLayer >+>
      AllProgramsLayer >+>
      Clock.live

  override def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] =
    runHttpApp
      .catchSome { case NonFatal(e) => error(">>> Error <<<", e) }
      .provideCustomLayer(HttpAppLayer)
      .exitCode

  private def runHttpApp: RIO[HttpAppEnv, Unit] =
    ZIO.runtime[HttpAppEnv].flatMap { implicit rt =>
      for {
        config <- serverConfig
        ex     <- executionContext

        controllers = makeAllApis[HttpAppEnv]
        app = addCors(addLogger(makeHttpApp(controllers)))

        _ <- runHttpServer[RIO[HttpAppEnv, *]](config, app, ex)
      } yield ()
    }
}
