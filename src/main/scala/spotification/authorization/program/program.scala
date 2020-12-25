package spotification.authorization

import spotification.authorization.service.{RefreshTokenServiceEnv, RequestTokenServiceEnv, refreshToken, requestToken}
import spotification.common.{NonBlankStringR, UriString}
import spotification.config.service.{AuthorizationConfigEnv, authorizationConfig}
import spotification.effect.{leftStringEitherToRIO, refineRIO, refineTask}
import zio.{RIO, Task}

package object program {
  type AuthorizeCallbackProgramEnv = AuthorizationConfigEnv with RequestTokenServiceEnv
  def authorizeCallbackProgram(rawCode: String): RIO[AuthorizeCallbackProgramEnv, AccessTokenResponse] =
    for {
      config <- authorizationConfig
      code   <- refineRIO[AuthorizationConfigEnv, NonBlankStringR](rawCode)
      resp   <- requestToken(AccessTokenRequest.make(config, code))
    } yield resp

  def authorizeCallbackErrorProgram(error: String): Task[AuthorizeErrorResponse] =
    refineTask[NonBlankStringR](error).map(AuthorizeErrorResponse)

  def makeAuthorizeUriProgram: RIO[AuthorizationConfigEnv, UriString] =
    authorizationConfig
      .map(config => makeAuthorizeUri(config.authorizeUri, AuthorizeRequest.make(config)))
      .flatMap(leftStringEitherToRIO)

  type RequestAccessTokenProgramEnv = AuthorizationConfigEnv with RefreshTokenServiceEnv
  def requestAccessTokenProgram(token: RefreshToken): RIO[RequestAccessTokenProgramEnv, AccessToken] =
    for {
      config <- authorizationConfig

      req = RefreshTokenRequest(
        config.clientId,
        config.clientSecret,
        RefreshTokenGrantType.RefreshToken,
        token
      )

      resp <- refreshToken(req)
    } yield resp.access_token
}
