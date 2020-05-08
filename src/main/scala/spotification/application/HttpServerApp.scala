package spotification.application

import spotification.infra.concurrent.ExecutionContextModule
import spotification.infra.config.ServerConfigModule
import spotification.infra.httpserver.HttpServer.{addCors, addLogger, httpApp, runHttpServer}
import spotification.infra.httpserver.HttpServerEnv
import spotification.presentation.Presentation.allRoutes
import zio.interop.catz._
import zio.{RIO, ZIO}

object HttpServerApp {
  val runHttpApp: RIO[HttpServerEnv, Unit] = ZIO.runtime[HttpServerEnv].flatMap { implicit rt =>
    for {
      config <- ServerConfigModule.config
      ex     <- ExecutionContextModule.executionContext
      app = addCors(addLogger(httpApp(allRoutes[HttpServerEnv])))
      _ <- runHttpServer[RIO[HttpServerEnv, *]](config, app, ex)
    } yield ()
  }
}
