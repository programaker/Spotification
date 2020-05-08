package spotification.infra

import cats.data.Kleisli
import org.http4s.{Request, Response}
import spotification.infra.concurrent.ExecutionContextModule
import spotification.infra.config.ServerConfigModule
import spotification.presentation.PresentationEnv
import zio.ZLayer

package object httpserver {
  type HttpApp[F[_]] = Kleisli[F, Request[F], Response[F]]

  type HttpServerEnv = ServerConfigModule with ExecutionContextModule with PresentationEnv
  object HttpServerEnv {
    val layer: ZLayer[ConfigServiceAndHttpClientEnv, Throwable, HttpServerEnv] =
      ServerConfigModule.layer ++ ExecutionContextModule.layer ++ PresentationEnv.layer
  }
}
