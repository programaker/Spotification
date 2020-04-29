package spotification.infra

import cats.data.Kleisli
import org.http4s.{Request, Response}
import spotification.core.BaseEnv
import spotification.core.config.ConfigModule.ServerConfigService
import spotification.presentation.PresentationEnv
import zio.RIO

package object httpserver {
  type HttpApp[F[_]] = Kleisli[F, Request[F], Response[F]]
  type HttpServerEnv = PresentationEnv with ServerConfigService with BaseEnv
  type HttpServerIO[A] = RIO[HttpServerEnv, A]
}
