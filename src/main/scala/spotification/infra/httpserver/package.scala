package spotification.infra

import cats.Monad
import cats.data.Kleisli
import cats.effect.{Concurrent, ConcurrentEffect, Timer}
import org.http4s.{HttpRoutes, Request, Response}
import org.http4s.server.Router
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger
import spotification.core.config.ServerConfig
import eu.timepit.refined.auto._

package object httpserver {

  type HttpApp[F[_]] = Kleisli[F, Request[F], Response[F]]

  def httpApp[F[_]: Monad](routes: Seq[(String, HttpRoutes[F])]): HttpApp[F] =
    Router(routes: _*).orNotFound

  def addLogger[F[_]: Concurrent](httpApp: HttpApp[F]): HttpApp[F] =
    Logger.httpApp(logHeaders = true, logBody = true)(httpApp)

  def runHttpServerF[F[_]: ConcurrentEffect: Timer](serverConfig: ServerConfig, httpApp: HttpApp[F]): F[Unit] =
    BlazeServerBuilder[F]
      .bindHttp(serverConfig.port, serverConfig.host)
      .withHttpApp(httpApp)
      .serve
      .compile
      .drain

}
