package spotification.infra

import cats.Monad
import cats.data.Kleisli
import org.http4s.{HttpRoutes, Request, Response}
import org.http4s.server.Router
import org.http4s.implicits._

package object httpserver {

  def httpApp[F[_]: Monad](routes: Seq[(String, HttpRoutes[F])]): Kleisli[F, Request[F], Response[F]] =
    Router(routes: _*).orNotFound

}
