package spotification.presentation

import org.http4s.HttpRoutes
import zio.Task
import zio.interop.catz._
import org.http4s.implicits._
import cats.data.Kleisli
import org.http4s.Request
import org.http4s.Response

object AuthorizationRoutes extends H4sDsl {
  private val EndPoint: String = "authorize"

  val routes: Kleisli[Task, Request[Task], Response[Task]] =
    HttpRoutes
      .of[Task] {
        case GET -> Root / EndPoint              => ???
        case GET -> Root / EndPoint / "callback" => ???
      }
      .orNotFound
}
