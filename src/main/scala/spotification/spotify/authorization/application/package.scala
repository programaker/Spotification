package spotification.spotify.authorization

import cats.implicits._
import spotification.spotify.authorization.domain._
import spotification.spotify.authorization.infra.httpclient._
import zio.RIO
import spotification.config.infra.Config
import spotification.common.domain.NonBlankStringR
import zio.interop.catz._

package object application {

  type AuthorizationEnv = Config with Authorization
  type AuthorizationIO[A] = RIO[AuthorizationEnv, A]

  val authorizeProgram: AuthorizationIO[Unit] =
    Config.readConfig.map(buildAuthorizeRequest).flatMap(Authorization.authorize)

  // TODO => do state validation
  def authorizationCallbackProgram(rawCode: String, rawState: Option[String]): AuthorizationIO[AccessTokenResponse] = {
    val config = Config.readConfig
    val code = refineRIO[NonBlankStringR, Config, String](rawCode)
    (config, code).mapN(buildAccessTokenRequest).flatMap(Authorization.requestToken)
  }

}
