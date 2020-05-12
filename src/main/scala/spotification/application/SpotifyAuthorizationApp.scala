package spotification.application

import cats.implicits._
import org.http4s.Uri
import spotification.domain.NonBlankStringR
import spotification.domain.spotify.authorization.{
  AccessTokenRequest,
  AccessTokenResponse,
  AuthorizeErrorResponse,
  AuthorizeRequest
}
import spotification.infra.Infra.refineRIO
import spotification.infra.config.AuthorizationConfigModule
import spotification.infra.httpclient.AuthorizationHttpClient.makeAuthorizeUri
import spotification.infra.spotify.authorization.AuthorizationModule
import zio.RIO
import zio.interop.catz._

object SpotifyAuthorizationApp {
  val makeAuthorizeUriProgram: RIO[SpotifyAuthorizationEnv, Uri] =
    for {
      config <- AuthorizationConfigModule.config
      resp   <- makeAuthorizeUri(AuthorizeRequest.make(config))
    } yield resp

  def authorizeCallbackProgram(rawCode: String): RIO[SpotifyAuthorizationEnv, AccessTokenResponse] = {
    val config = AuthorizationConfigModule.config
    val code = refineRIO[NonBlankStringR, AuthorizationConfigModule, String](rawCode)
    (config, code).mapN(AccessTokenRequest.make).flatMap(AuthorizationModule.requestToken)
  }

  def authorizeCallbackErrorProgram(error: String): RIO[SpotifyAuthorizationEnv, AuthorizeErrorResponse] =
    refineRIO[NonBlankStringR, SpotifyAuthorizationEnv, String](error).map(AuthorizeErrorResponse)
}
