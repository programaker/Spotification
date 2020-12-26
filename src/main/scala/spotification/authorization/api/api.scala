package spotification.authorization

import cats.implicits.toTraverseOps
import eu.timepit.refined.auto._
import org.http4s.AuthScheme.Bearer
import org.http4s.Credentials.Token
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.{Authorization, Location}
import org.http4s.{HttpRoutes, Request, Uri}
import spotification.authorization.httpclient.{RefreshTokenServiceLayer, RequestTokenServiceLayer}
import spotification.authorization.json.implicits.{AccessTokenResponseEncoder, AuthorizeErrorResponseEncoder}
import spotification.authorization.program._
import spotification.common.NonBlankStringR
import spotification.common.api.handleGenericError
import spotification.common.httpclient.HttpClientEnv
import spotification.common.json.implicits.entityEncoderF
import spotification.config.service.AuthorizationConfigEnv
import spotification.config.source.AuthorizationConfigLayer
import spotification.effect.refineRIO
import zio.interop.catz.{deferInstance, monadErrorInstance}
import zio.{RIO, RLayer, ZIO}

package object api {
  val AuthorizeCallbackProgramLayer: RLayer[AuthorizationConfigEnv with HttpClientEnv, AuthorizeCallbackProgramEnv] =
    AuthorizationConfigLayer ++ RequestTokenServiceLayer

  val RequestAccessTokenProgramLayer: RLayer[AuthorizationConfigEnv with HttpClientEnv, RequestAccessTokenProgramEnv] =
    AuthorizationConfigLayer ++ RefreshTokenServiceLayer

  val AuthorizationProgramsLayer: RLayer[AuthorizationConfigEnv with HttpClientEnv, AuthorizationProgramsEnv] =
    AuthorizeCallbackProgramLayer ++ RequestAccessTokenProgramLayer

  def makeAuthorizationApi[R <: AuthorizationProgramsEnv]: HttpRoutes[RIO[R, *]] = {
    val Callback: String = "callback"

    val dsl: Http4sDsl[RIO[R, *]] = Http4sDsl[RIO[R, *]]
    import dsl._

    HttpRoutes.of[RIO[R, *]] {
      case GET -> Root =>
        makeAuthorizeUriProgram.foldM(
          handleGenericError(dsl, _),
          uri => Found(Location(Uri.unsafeFromString(uri)))
        )

      case GET -> Root / Callback :? CodeQP(code) +& StateQP(_) =>
        authorizeCallbackProgram(code).foldM(handleGenericError(dsl, _), Ok(_))

      case GET -> Root / Callback :? ErrorQP(error) +& StateQP(_) =>
        authorizeCallbackErrorProgram(error).foldM(handleGenericError(dsl, _), Ok(_))
    }
  }

  def requiredRefreshTokenFromRequest[R](req: Request[RIO[R, *]]): RIO[R, RefreshToken] =
    refreshTokenFromRequest(req).flatMap {
      case Some(refreshToken) => ZIO.succeed(refreshToken)
      case None               => ZIO.fail(new Exception("Bearer refresh token absent from request"))
    }

  def refreshTokenFromRequest[R](req: Request[RIO[R, *]]): RIO[R, Option[RefreshToken]] =
    req.headers
      .get(Authorization)
      .map(_.credentials)
      .flatMap {
        case Token(Bearer, refreshTokenString) => Some(refreshTokenString)
        case _                                 => None
      }
      .map(refineRIO[R, NonBlankStringR](_))
      .sequence
      .map(_.map(RefreshToken(_)))
}
