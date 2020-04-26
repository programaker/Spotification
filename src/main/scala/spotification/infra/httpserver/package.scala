package spotification.infra

import cats.data.Kleisli
import org.http4s.{Request, Response}
import spotification.presentation.PresentationEnv
import zio.RIO
import zio.clock.Clock

//import spotification.core.config.ServerConfigModule
//import spotification.presentation.{PresentationEnv, PresentationModule}
//import zio.clock.Clock
//import zio.interop.catz._
//import zio.{RIO, ZIO}

package object httpserver {
  type HttpApp[F[_]] = Kleisli[F, Request[F], Response[F]]

  type HttpServerEnv = PresentationEnv with Clock
  type HttpServerIO[A] = RIO[HttpServerEnv, A]
}
