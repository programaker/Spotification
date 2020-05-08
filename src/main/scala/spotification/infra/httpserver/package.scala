package spotification.infra

import cats.data.Kleisli
import org.http4s.{Request, Response}
import spotification.infra.concurrent.ExecutionContextModule
import spotification.infra.config.ServerConfigModule
import spotification.presentation.PresentationEnv

package object httpserver {
  type HttpApp[F[_]] = Kleisli[F, Request[F], Response[F]]
  type HttpServerModule = ServerConfigModule with ExecutionContextModule with PresentationEnv
}
