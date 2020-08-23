package spotification.presentation

import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.Location
import zio.RIO
import spotification.application.spotifyauthorization._
import spotification.presentation._
import io.circe.refined._
import io.circe.generic.auto._
import zio.interop.catz._
import spotification.infra.json.implicits._

final class AuthorizationController[R <: SpotifyAuthorizationEnv] {
  private val Callback: String = "callback"

  private val H4sDsl: Http4sDsl[RIO[R, *]] = Http4sDsl[RIO[R, *]]
  import H4sDsl._

  private object CodeQP extends QueryParamDecoderMatcher[String]("code")
  private object ErrorQP extends QueryParamDecoderMatcher[String]("error")
  private object StateQP extends OptionalQueryParamDecoderMatcher[String]("state")

  val routes: HttpRoutes[RIO[R, *]] = HttpRoutes.of[RIO[R, *]] {
    case GET -> Root =>
      makeAuthorizeUriProgram.foldM(handleGenericError(H4sDsl, _), uri => Found(Location(uri)))

    case GET -> Root / Callback :? CodeQP(code) +& StateQP(_) =>
      authorizeCallbackProgram(code).foldM(handleGenericError(H4sDsl, _), Ok(_))

    case GET -> Root / Callback :? ErrorQP(error) +& StateQP(_) =>
      authorizeCallbackErrorProgram(error).foldM(handleGenericError(H4sDsl, _), Ok(_))
  }
}
