package spotification.presentation

import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import Presentation._
import org.http4s.headers.Location
import spotification.infra.httpclient.AuthorizationHttpClient.makeAuthorizeUriProgram
import zio.RIO
import spotification.application.SpotifyAuthorizationApp._
import spotification.infra.spotify.authorization.AuthorizationEnv

// ==========
// Despite IntelliJ telling that
// `import io.circe.refined._`
// `import io.circe.generic.auto._`
// `import zio.interop.catz._`
// `import spotification.infra.json._`
// are not being used, they are required to compile
// ==========
import io.circe.refined._
import io.circe.generic.auto._
import zio.interop.catz._
import spotification.infra.Json.Implicits._

final class AuthorizationController[R <: AuthorizationEnv] {
  private val Callback: String = "callback"

  private val H4sAuthorizationDsl: Http4sDsl[RIO[R, *]] = Http4sDsl[RIO[R, *]]
  import H4sAuthorizationDsl._

  private object CodeQP extends QueryParamDecoderMatcher[String]("code")
  private object ErrorQP extends QueryParamDecoderMatcher[String]("error")
  private object StateQP extends OptionalQueryParamDecoderMatcher[String]("state")

  val routes: HttpRoutes[RIO[R, *]] = HttpRoutes.of[RIO[R, *]] {
    case GET -> Root =>
      makeAuthorizeUriProgram.foldM(
        handleGenericError(H4sAuthorizationDsl, _),
        uri => Found(Location(uri))
      )

    case GET -> Root / Callback :? CodeQP(code) +& StateQP(_) =>
      authorizeCallbackProgram(code).foldM(handleGenericError(H4sAuthorizationDsl, _), Ok(_))

    case GET -> Root / Callback :? ErrorQP(error) +& StateQP(_) =>
      authorizeCallbackErrorProgram(error).foldM(handleGenericError(H4sAuthorizationDsl, _), Ok(_))
  }
}
