package spotification.infra.httpserver

import cats.Monad
import cats.effect.{Concurrent, ConcurrentEffect, Timer}
import eu.timepit.refined.auto._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.{CORS, Logger}
import spotification.application.ConfigModule
import spotification.core.config.ServerConfig
import spotification.presentation.Presentation.allRoutes
import spotification.presentation.Routes
import zio.{RIO, ZIO}
import zio.interop.catz._

object HttpServer {
  val runHttpApp: RIO[HttpServerEnv, Unit] = ZIO.runtime[HttpServerEnv].flatMap { implicit rt =>
    for {
      config <- ConfigModule.serverConfig
      _      <- runHttpServer[HttpServerIO](config, addCors(addLogger(httpApp(allRoutes[HttpServerEnv]))))
    } yield ()
  }

  private def runHttpServer[F[_]: ConcurrentEffect: Timer](serverConfig: ServerConfig, httpApp: HttpApp[F]): F[Unit] =
    BlazeServerBuilder[F]
      .bindHttp(serverConfig.port, serverConfig.host)
      .withHttpApp(httpApp)
      .serve
      .compile
      .drain

  private def httpApp[F[_]: Monad](routes: Routes[F]): HttpApp[F] =
    Router(routes: _*).orNotFound

  private def addLogger[F[_]: Concurrent](httpApp: HttpApp[F]): HttpApp[F] =
    Logger.httpApp(logHeaders = true, logBody = true)(httpApp)

  private def addCors[F[_]: Concurrent](httpApp: HttpApp[F]): HttpApp[F] =
    CORS(httpApp)
}
