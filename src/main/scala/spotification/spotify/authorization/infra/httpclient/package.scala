package spotification.spotify.authorization.infra

import org.http4s.AuthScheme.{Basic, Bearer}
import org.http4s.Credentials.Token
import spotification.common.infra.httpclient.H4sClient
import zio.ZLayer
import zio.Has
import zio.ZIO
import spotification.spotify.authorization.domain._
import zio.RIO
import zio.Task
import eu.timepit.refined.auto._

package object httpclient {

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

    val AuthorizationLayer: ZLayer[H4sClient, Nothing, Authorization] =
      ZLayer.fromFunction(new H4sAuthorizationService(_))
  }

  type H4sAuthorization = org.http4s.headers.Authorization
  val H4sAuthorization: org.http4s.headers.Authorization.type = org.http4s.headers.Authorization

  def authorizationBasicHeader(clientId: ClientId, clientSecret: ClientSecret): H4sAuthorization =
    H4sAuthorization(Token(Basic, base64Credentials(clientId, clientSecret)))

  def authorizationBearerHeader(accessToken: AccessToken): H4sAuthorization =
    H4sAuthorization(Token(Bearer, accessToken.value))

}
