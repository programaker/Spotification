package spotification

import cats.Monad
import cats.data.Kleisli
import cats.effect.Concurrent
import cats.effect.kernel.Async
import eu.timepit.refined.auto._
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.middleware.{CORS, Logger}
import org.http4s.{Request, Response}
import spotification.common.api.Routes
import spotification.config.ServerConfig

package object httpserver {
  type HttpApp[F[_]] = Kleisli[F, Request[F], Response[F]]

  def runHttpServer[F[_]: Async](
    serverConfig: ServerConfig,
    httpApp: HttpApp[F]
  ): F[Unit] =
    BlazeServerBuilder[F]
      .bindHttp(serverConfig.port, serverConfig.host)
      .withHttpApp(httpApp)
      .serve
      .compile
      .drain

  def makeHttpApp[F[_]: Monad](routes: Routes[F]): HttpApp[F] =
    Router(routes: _*).orNotFound

  def addLogger[F[_]: Async](httpApp: HttpApp[F]): HttpApp[F] =
    Logger.httpApp(logHeaders = true, logBody = true)(httpApp)

  def addCors[F[_]: Concurrent](httpApp: HttpApp[F]): HttpApp[F] =
    CORS(httpApp)
}
