package spotification.api

import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.Location
import spotification.common.infra.json.implicits._
import zio.RIO
import zio.interop.catz.{deferInstance, monadErrorInstance}
import io.circe.refined._
import spotification.spotify.authorization.application.spotifyauthorizarion.{
  SpotifyAuthorizationEnv,
  authorizeCallbackErrorProgram,
  authorizeCallbackProgram,
  makeAuthorizeUriProgram
}

final class SpotifyAuthorizationController[R <: SpotifyAuthorizationEnv] extends Http4sDsl[RIO[R, *]] {
  private val Callback: String = "callback"

  private object CodeQP extends QueryParamDecoderMatcher[String]("code")
  private object ErrorQP extends QueryParamDecoderMatcher[String]("error")
  private object StateQP extends OptionalQueryParamDecoderMatcher[String]("state")

  val routes: HttpRoutes[RIO[R, *]] = HttpRoutes.of[RIO[R, *]] {
    case GET -> Root =>
      makeAuthorizeUriProgram.foldM(handleGenericError(this, _), uri => Found(Location(uri)))

    case GET -> Root / Callback :? CodeQP(code) +& StateQP(_) =>
      authorizeCallbackProgram(code).foldM(handleGenericError(this, _), Ok(_))

    case GET -> Root / Callback :? ErrorQP(error) +& StateQP(_) =>
      authorizeCallbackErrorProgram(error).foldM(handleGenericError(this, _), Ok(_))
  }
}
