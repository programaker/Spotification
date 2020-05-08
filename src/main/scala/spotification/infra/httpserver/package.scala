package spotification.infra

import cats.data.Kleisli
import org.http4s.{Request, Response}

package object httpserver {
  type HttpApp[F[_]] = Kleisli[F, Request[F], Response[F]]
}
