package spotification.spotify.authorization.presentation

import org.http4s.{Entity, EntityEncoder, Headers, HttpRoutes}
import spotification.spotify.authorization.application._
import zio.interop.catz._
import io.circe.generic.auto._
import io.circe.refined._
import spotification.spotify.authorization.domain.AccessTokenResponse

object AuthorizationController {
  private val EndPoint: String = "authorize"
  private val Callback: String = "callback"

  import H4sAuthorizationDsl._

  val authorizeRoutes: HttpRoutes[AuthorizationIO] = HttpRoutes.of[AuthorizationIO] {
    case GET -> Root / EndPoint =>
      authorizeProgram.foldM(handleError, _ => Ok())

    case GET -> Root / EndPoint / Callback :? CodeQP(code) +& StateQP(state) =>
      authorizationCallbackProgram(code, state).foldM(handleError, resp => Ok(resp))

    case GET -> Root / EndPoint / Callback :? ErrorQP(error) +& StateQP(state) =>
      Ok()
  }

  private def handleError(e: Throwable): AuthorizationResponse = InternalServerError(e.getMessage)
}
