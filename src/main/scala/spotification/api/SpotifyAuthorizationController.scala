package spotification.api

import io.circe.generic.auto._
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.Location
import org.http4s.{HttpRoutes, Uri}
import eu.timepit.refined.auto._
import spotification.core.authorization.program.{
  SpotifyAuthorizationEnv,
  authorizeCallbackErrorProgram,
  authorizeCallbackProgram,
  makeAuthorizeUriProgram
}
import spotification.json.implicits.entityEncoderF
import zio.RIO
import zio.interop.catz.{deferInstance, monadErrorInstance}

final class SpotifyAuthorizationController[R <: SpotifyAuthorizationEnv] extends Http4sDsl[RIO[R, *]] {
  private val Callback: String = "callback"

  private object CodeQP extends QueryParamDecoderMatcher[String]("code")
  private object ErrorQP extends QueryParamDecoderMatcher[String]("error")
  private object StateQP extends OptionalQueryParamDecoderMatcher[String]("state")

  val routes: HttpRoutes[RIO[R, *]] = HttpRoutes.of[RIO[R, *]] {
    case GET -> Root =>
      makeAuthorizeUriProgram.foldM(
        handleGenericError(this, _),
        uri => Found(Location(Uri.unsafeFromString(uri)))
      )

    case GET -> Root / Callback :? CodeQP(code) +& StateQP(_) =>
      authorizeCallbackProgram(code).foldM(handleGenericError(this, _), Ok(_))

    case GET -> Root / Callback :? ErrorQP(error) +& StateQP(_) =>
      authorizeCallbackErrorProgram(error).foldM(handleGenericError(this, _), Ok(_))
  }
}
