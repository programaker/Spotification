package spotification.infra.httpserver

//import cats.Monad
//import cats.data.Kleisli
//import cats.effect.{Concurrent, ConcurrentEffect, Timer}
//import eu.timepit.refined.auto._
//import org.http4s.implicits._
//import org.http4s.server.Router
//import org.http4s.server.blaze.BlazeServerBuilder
//import org.http4s.server.middleware.Logger
//import org.http4s.{Request, Response}
//import spotification.core.config.ServerConfig
//import spotification.presentation.Routes

private[httpserver] trait ApplicationM {

//  type HttpApp[F[_]] = Kleisli[F, Request[F], Response[F]]
//
//  def httpApp[F[_]: Monad](routes: Routes[F]): HttpApp[F] =
//    Router(routes: _*).orNotFound
//
//  def addLogger[F[_]: Concurrent](httpApp: HttpApp[F]): HttpApp[F] =
//    Logger.httpApp(logHeaders = true, logBody = true)(httpApp)
//
//  def runHttpServer[F[_]: ConcurrentEffect: Timer](serverConfig: ServerConfig, httpApp: HttpApp[F]): F[Unit] =
//    BlazeServerBuilder[F]
//      .bindHttp(serverConfig.port, serverConfig.host)
//      .withHttpApp(httpApp)
//      .serve
//      .compile
//      .drain

}
