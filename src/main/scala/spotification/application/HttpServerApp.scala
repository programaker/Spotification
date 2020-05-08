package spotification.application

import spotification.infra.concurrent.ExecutionContextZIO
import spotification.infra.config.ConfigZIO
import spotification.infra.httpserver.HttpServer.{addCors, addLogger, httpApp, runHttpServer}
import spotification.infra.httpserver.HttpServerZIO.HttpServerEnv
import spotification.presentation.Presentation.allRoutes
import zio.interop.catz._
import zio.{RIO, ZIO}

object HttpServerApp {
  val runHttpApp: RIO[HttpServerEnv, Unit] = ZIO.runtime[HttpServerEnv].flatMap { implicit rt =>
    for {
      config <- ConfigZIO.serverConfig
      ex     <- ExecutionContextZIO.executionContext
      app = addCors(addLogger(httpApp(allRoutes[HttpServerEnv])))
      _ <- runHttpServer[RIO[HttpServerEnv, *]](config, app, ex)
    } yield ()
  }
}
