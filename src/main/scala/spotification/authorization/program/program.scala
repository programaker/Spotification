package spotification.authorization

import spotification.authorization.service.{RefreshTokenServiceR, RequestTokenServiceR, refreshToken, requestToken}
import spotification.common.{NonBlankStringP, UriString}
import spotification.config.service.{AuthorizationConfigR, authorizationConfig}
import spotification.effect.{eitherToRIO, refineRIO, refineTask}
import zio.{RIO, Task}

package object program {
  type AuthorizeCallbackProgramR = AuthorizationConfigR with RequestTokenServiceR
  type RequestAccessTokenProgramR = AuthorizationConfigR with RefreshTokenServiceR
  type AuthorizationProgramsR = AuthorizeCallbackProgramR with RequestAccessTokenProgramR

  def authorizeCallbackProgram(rawCode: String): RIO[AuthorizeCallbackProgramR, AccessTokenResponse] =
    for {
      config <- authorizationConfig
      code   <- refineRIO[AuthorizationConfigR, NonBlankStringP](rawCode)
      resp   <- requestToken(AccessTokenRequest.make(config, code))
    } yield resp

  def authorizeCallbackErrorProgram(error: String): Task[AuthorizeErrorResponse] =
    refineTask[NonBlankStringP](error).map(AuthorizeErrorResponse)

  def makeAuthorizeUriProgram: RIO[AuthorizationConfigR, UriString] =
    authorizationConfig
      .map(config => makeAuthorizeUri(config.authorizeUri, AuthorizeRequest.make(config)))
      .flatMap(eitherToRIO)

  def requestAccessTokenProgram(token: RefreshToken): RIO[RequestAccessTokenProgramR, AccessToken] =
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
