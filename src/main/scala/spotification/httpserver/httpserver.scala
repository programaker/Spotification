package spotification

import cats.Monad
import cats.data.Kleisli
import cats.effect.{Concurrent, ConcurrentEffect, Timer}
import eu.timepit.refined.auto._
import org.http4s.{Request, Response}
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.{CORS, Logger}
import spotification.common.api.Routes
import spotification.config.ServerConfig

import scala.concurrent.ExecutionContext

package object httpserver {
  type HttpApp[F[_]] = Kleisli[F, Request[F], Response[F]]

  def runHttpServer[F[_]: ConcurrentEffect: Timer](
    serverConfig: ServerConfig,
    httpApp: HttpApp[F],
    ex: ExecutionContext
  ): F[Unit] =
    BlazeServerBuilder[F](ex)
      .bindHttp(serverConfig.port, serverConfig.host)
      .withHttpApp(httpApp)
      .serve
      .compile
      .drain

  def makeHttpApp[F[_]: Monad](routes: Routes[F]): HttpApp[F] =
    Router(routes: _*).orNotFound

  def addLogger[F[_]: Concurrent](httpApp: HttpApp[F]): HttpApp[F] =
    Logger.httpApp(logHeaders = true, logBody = true)(httpApp)

  def addCors[F[_]: Concurrent](httpApp: HttpApp[F]): HttpApp[F] =
    CORS(httpApp)
}
