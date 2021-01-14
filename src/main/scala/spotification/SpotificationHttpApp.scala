package spotification

import spotification.common.api.{AllProgramsLayer, makeAllApis}
import spotification.common.httpclient.HttpClientLayer
import spotification.common.program.AllProgramsR
import spotification.concurrent.{ExecutionContextLayer, ExecutionContextR, executionContext}
import spotification.config.service.{ServerConfigR, serverConfig}
import spotification.config.source._
import spotification.httpserver.{addCors, addLogger, makeHttpApp, runHttpServer}
import spotification.log.service.error
import zio._
import zio.clock.Clock
import zio.interop.catz._

import scala.util.control.NonFatal

object SpotificationHttpApp extends zio.App {
  type HttpAppR = ServerConfigR with ExecutionContextR with AllProgramsR with Clock

  val HttpAppLayer: TaskLayer[HttpAppR] =
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

  private def runHttpApp: RIO[HttpAppR, Unit] =
    ZIO.runtime[HttpAppR].flatMap { implicit rt =>
      for {
        config <- serverConfig
        ex     <- executionContext

        controllers = makeAllApis[HttpAppR]
        app = addCors(addLogger(makeHttpApp(controllers)))

        _ <- runHttpServer[RIO[HttpAppR, *]](config, app, ex)
      } yield ()
    }
}
