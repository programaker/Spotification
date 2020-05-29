package spotification.presentation

import spotification.infra.concurrent.ExecutionContextModule
import spotification.infra.config.ServerConfigModule
import spotification.infra.httpserver.HttpServer.{addCors, addLogger, httpApp, runHttpServer}
import zio.interop.catz._
import zio.{RIO, ZIO}

object Presentation {
  val runHttpApp: RIO[HttpAppEnv, Unit] = ZIO.runtime[HttpAppEnv].flatMap { implicit rt =>
    for {
      config <- ServerConfigModule.config
      ex     <- ExecutionContextModule.executionContext
      controllers = allRoutes[HttpAppEnv]
      app = addCors(addLogger(httpApp(controllers)))
      _ <- runHttpServer[RIO[HttpAppEnv, *]](config, app, ex)
    } yield ()
  }

  def allRoutes[R <: PresentationEnv]: Routes[RIO[R, *]] =
    Seq(
      "/health"          -> new HealthCheckController[R].routes,
      "/authorization"   -> new AuthorizationController[R].routes,
      "/release-radar"   -> new ReleaseRadarController[R].routes,
      "/merge-playlists" -> new MergePlaylistsController[R].routes
    )
}
