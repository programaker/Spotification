package spotification.presentation

import org.http4s.HttpRoutes
import zio.Task
import zio.interop.catz._

object AuthorizationController extends H4sDsl {
  private val EndPoint: String = "authorize"

  val authorizationRoutes: HttpRoutes[Task] =
    HttpRoutes.of[Task] {
      case GET -> Root / EndPoint              => ???
      case GET -> Root / EndPoint / "callback" => ???
    }
}
