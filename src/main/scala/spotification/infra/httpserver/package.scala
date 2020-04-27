package spotification.infra

import cats.data.Kleisli
import org.http4s.{Request, Response}
import spotification.core.config.ConfigModule.ServerConfigService
import spotification.presentation.PresentationEnv
import zio.RIO
import zio.clock.Clock

package object httpserver {
  type HttpApp[F[_]] = Kleisli[F, Request[F], Response[F]]
  type HttpServerEnv = PresentationEnv with ServerConfigService with Clock
  type HttpServerIO[A] = RIO[HttpServerEnv, A]
}
