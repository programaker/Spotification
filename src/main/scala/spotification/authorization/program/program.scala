package spotification.authorization

import spotification.authorization.service.{AuthorizationServiceEnv, refreshToken, requestToken}
import spotification.common.{NonBlankStringR, UriString}
import spotification.config.service.{AuthorizationConfigEnv, authorizationConfig}
import spotification.effect.{leftStringEitherToRIO, refineRIO}
import zio.RIO

package object program {
  type SpotifyAuthorizationEnv = AuthorizationServiceEnv with AuthorizationConfigEnv

  def authorizeCallbackProgram(rawCode: String): RIO[SpotifyAuthorizationEnv, AccessTokenResponse] =
    for {
      config <- authorizationConfig
      code   <- refineRIO[AuthorizationConfigEnv, NonBlankStringR](rawCode)
      resp   <- requestToken(AccessTokenRequest.make(config, code))
    } yield resp

  def authorizeCallbackErrorProgram(error: String): RIO[SpotifyAuthorizationEnv, AuthorizeErrorResponse] =
    refineRIO[SpotifyAuthorizationEnv, NonBlankStringR](error).map(AuthorizeErrorResponse)

  def makeAuthorizeUriProgram: RIO[SpotifyAuthorizationEnv, UriString] =
    authorizationConfig
      .map(config => makeAuthorizeUri(config.authorizeUri, AuthorizeRequest.make(config)))
      .flatMap(leftStringEitherToRIO)

  def requestAccessTokenProgram(token: RefreshToken): RIO[SpotifyAuthorizationEnv, AccessToken] =
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
