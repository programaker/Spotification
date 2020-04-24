package spotification.core.spotify

import cats.implicits._
import eu.timepit.refined._
import eu.timepit.refined.cats._
import eu.timepit.refined.api.{Refined, Validate}
import eu.timepit.refined.auto._
import eu.timepit.refined.generic.Equal
import spotification.core._
import spotification.core.config.{AppConfig, Config}
import zio.{Has, IO, RIO, Task, ZIO}

// ==========
// Despite IntelliJ telling that `import zio.interop.catz._` is not being used,
// it is required to make mapN work
// ==========
import zio.interop.catz._

package object authorization extends Scope {

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

  type AuthorizationEnv = Config with Authorization
  type AuthorizationIO[A] = RIO[AuthorizationEnv, A]

  final case class AccessToken(value: NonBlankString) extends Val[NonBlankString]
  final case class RefreshToken(value: NonBlankString) extends Val[NonBlankString]

  final case class ClientId(value: HexString32) extends Val[HexString32]
  final case class ClientSecret(value: HexString32) extends Val[HexString32]

  type Authorization = Has[Authorization.Service]
  object Authorization {
    trait Service {
      def authorize(req: AuthorizeRequest): Task[Unit]
      def requestToken(req: AccessTokenRequest): Task[AccessTokenResponse]
      def refreshToken(req: RefreshTokenRequest): Task[RefreshTokenResponse]
    }

    def authorize(req: AuthorizeRequest): RIO[Authorization, Unit] =
      ZIO.accessM(_.get.authorize(req))

    def requestToken(req: AccessTokenRequest): RIO[Authorization, AccessTokenResponse] =
      ZIO.accessM(_.get.requestToken(req))

    def refreshToken(req: RefreshTokenRequest): RIO[Authorization, RefreshTokenResponse] =
      ZIO.accessM(_.get.refreshToken(req))
  }

  def base64Credentials(clientId: ClientId, clientSecret: ClientSecret): String =
    base64(show"$clientId:$clientSecret")

  def refineZIO[P, R, A](a: A)(implicit v: Validate[A, P]): ZIO[R, String, Refined[A, P]] =
    ZIO.fromFunctionM(_ => IO.fromEither(refineV[P](a)))

  def refineRIO[P, R, A](a: A)(implicit v: Validate[A, P]): RIO[R, Refined[A, P]] =
    refineZIO[P, R, A](a).absorbWith(new Exception(_))

  def buildAuthorizeRequest(cfg: AppConfig): AuthorizeRequest = AuthorizeRequest(
    client_id = cfg.clientId,
    redirect_uri = cfg.redirectUri,
    response_type = AuthorizationResponseType.Code,
    state = None, //we'll not use it for now
    scope = cfg.scopes,
    show_dialog = None //defaults to false, which is what we want
  )

  def buildAccessTokenRequest(cfg: AppConfig, code: NonBlankString): AccessTokenRequest = AccessTokenRequest(
    client_id = cfg.clientId,
    client_secret = cfg.clientSecret,
    grant_type = AccessTokenGrantType.AuthorizationCode,
    code = code,
    redirect_uri = cfg.redirectUri
  )

  val authorizeProgram: AuthorizationIO[Unit] =
    Config.readConfig.map(buildAuthorizeRequest).flatMap(Authorization.authorize)

  // TODO => do state validation
  def authorizationCallbackProgram(rawCode: String, rawState: Option[String]): AuthorizationIO[AccessTokenResponse] = {
    val config = Config.readConfig
    val code = refineRIO[NonBlankStringR, Config, String](rawCode)
    (config, code).mapN(buildAccessTokenRequest).flatMap(Authorization.requestToken)
  }

}
