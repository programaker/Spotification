package spotification.presentation

import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import spotification.core.spotify.authorization.AuthorizationModule.AuthorizationIO
import spotification.core.spotify.authorization.Authorization._
import Presentation._

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
import spotification.infra.Json._

object AuthorizationController {
  private val Callback: String = "callback"

  private val H4sAuthorizationDsl: Http4sDsl[AuthorizationIO] = Http4sDsl[AuthorizationIO]
  import H4sAuthorizationDsl._

  private object CodeQP extends QueryParamDecoderMatcher[String]("code")
  private object ErrorQP extends QueryParamDecoderMatcher[String]("error")
  private object StateQP extends OptionalQueryParamDecoderMatcher[String]("state")

  val routes: HttpRoutes[AuthorizationIO] = HttpRoutes.of[AuthorizationIO] {
    case GET -> Root =>
      authorizeProgram.foldM(handleGenericError(H4sAuthorizationDsl, _), _ => Ok())

    case GET -> Root / Callback :? CodeQP(code) +& StateQP(_) =>
      authorizeCallbackProgram(code).foldM(handleGenericError(H4sAuthorizationDsl, _), Ok(_))

    case GET -> Root / Callback :? ErrorQP(error) +& StateQP(_) =>
      authorizeCallbackErrorProgram(error).foldM(handleGenericError(H4sAuthorizationDsl, _), Ok(_))
  }
}
