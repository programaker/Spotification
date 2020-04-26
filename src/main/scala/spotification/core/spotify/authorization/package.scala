package spotification.core.spotify

import java.nio.charset.StandardCharsets.UTF_8
import java.util.Base64

import cats.implicits._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.generic.Equal
import io.estatico.newtype.macros.newtype
import spotification.core._
import spotification.core.config.{SpotifyConfig, SpotifyConfigModule}

// ==========
// Despite IntelliJ telling that
// `import eu.timepit.refined.cats._`
// `import zio.interop.catz._`
// `import spotification.core.newtype._`
// are not being used, they are required to compile
// ==========
import eu.timepit.refined.cats._
import zio.interop.catz._
import spotification.core.Implicits._

package object authorization extends ScopeM with AuthorizationM {

  val authorizeProgram: AuthorizationIO[Unit] =
    SpotifyConfigModule.spotifyConfig.map(buildAuthorizeRequest).flatMap(AuthorizationModule.authorize)

  // TODO => do state validation
  def authorizeCallbackProgram(rawCode: String): AuthorizationIO[AccessTokenResponse] = {
    val config = SpotifyConfigModule.spotifyConfig
    val code = refineRIO[NonBlankStringR, SpotifyConfigModule, String](rawCode)
    (config, code).mapN(buildAccessTokenRequest).flatMap(AuthorizationModule.requestToken)
  }

  // TODO => do state validation
  def authorizeCallbackErrorProgram(error: String): AuthorizationIO[AuthorizeErrorResponse] =
    refineRIO[NonBlankStringR, AuthorizationEnv, String](error).map(AuthorizeErrorResponse)

  type AuthorizationResponseTypeR = Equal["code"] //it's the only one that appeared until now
  type AuthorizationResponseType = String Refined AuthorizationResponseTypeR
  object AuthorizationResponseType {
    val Code: AuthorizationResponseType = "code"
  }

  type AccessTokenGrantTypeR = Equal["authorization_code"]
  type AccessTokenGrantType = String Refined AccessTokenGrantTypeR
  object AccessTokenGrantType {
    val AuthorizationCode: AccessTokenGrantType = "authorization_code"
  }

  type RefreshTokenGrantTypeR = Equal["refresh_token"]
  type RefreshTokenGrantType = String Refined RefreshTokenGrantTypeR
  object RefreshTokenGrantType {
    val RefreshToken: RefreshTokenGrantType = "refresh_token"
  }

  type TokenTypeR = Equal["Bearer"]
  type TokenType = String Refined TokenTypeR
  object TokenType {
    val Bearer: TokenType = "Bearer"
  }

  @newtype case class AccessToken(value: NonBlankString)
  @newtype case class RefreshToken(value: NonBlankString)

  @newtype case class ClientId(value: HexString32)
  @newtype case class ClientSecret(value: HexString32)

  def base64Credentials(clientId: ClientId, clientSecret: ClientSecret): String =
    base64(show"$clientId:$clientSecret")

  def base64(s: String): String = Base64.getEncoder.encodeToString(s.getBytes(UTF_8))

  def buildAuthorizeRequest(cfg: SpotifyConfig): AuthorizeRequest = AuthorizeRequest(
    client_id = cfg.clientId,
    redirect_uri = cfg.redirectUri,
    response_type = AuthorizationResponseType.Code,
    state = None, //we'll not use it for now
    scope = cfg.scopes,
    show_dialog = None //defaults to false, which is what we want
  )

  def buildAccessTokenRequest(cfg: SpotifyConfig, code: NonBlankString): AccessTokenRequest = AccessTokenRequest(
    client_id = cfg.clientId,
    client_secret = cfg.clientSecret,
    grant_type = AccessTokenGrantType.AuthorizationCode,
    code = code,
    redirect_uri = cfg.redirectUri
  )

}
