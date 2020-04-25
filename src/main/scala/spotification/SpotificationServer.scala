package spotification

import cats.data.{Kleisli, OptionT}
import cats.effect.{ConcurrentEffect, Timer}
import cats.implicits._
import fs2.Stream
import org.http4s.{HttpRoutes, Request, Response}
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger

import scala.concurrent.ExecutionContext.global

object SpotificationServer {

  def stream[F[_]: ConcurrentEffect](implicit T: Timer[F]): Stream[F, Nothing] = {
    for {
      client <- BlazeClientBuilder[F](global).stream
      helloWorldAlg = HelloWorld.impl[F]
      jokeAlg = Jokes.impl[F](client)

      // Combine Service Routes into an HttpApp.
      // Can also be done via a Router if you
      // want to extract a segments not checked
      // in the underlying routes.
      helloWorldRoutes: HttpRoutes[F] = SpotificationRoutes.helloWorldRoutes[F](helloWorldAlg)
      jokeRoutes: HttpRoutes[F] = SpotificationRoutes.jokeRoutes[F](jokeAlg)
      allRoutes: Kleisli[OptionT[F, *], Request[F], Response[F]] = helloWorldRoutes <+> jokeRoutes
      httpApp: Kleisli[F, Request[F], Response[F]] = allRoutes.orNotFound

      // With Middlewares in place
      finalHttpApp = Logger.httpApp(logHeaders = true, logBody = true)(httpApp)

      exitCode <- BlazeServerBuilder[F]
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain

}
