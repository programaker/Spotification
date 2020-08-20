package spotification.infra.spotify

import cats.implicits._
import eu.timepit.refined.auto._
import io.circe.generic.auto._
import io.circe.jawn
import org.http4s.Method.POST
import org.http4s.headers.Accept
import org.http4s._
import spotification.domain.config.AuthorizationConfig
import spotification.domain.spotify.authorization._
import spotification.infra.json.implicits._
import spotification.infra.config.AuthorizationConfigModule
import spotification.infra.httpclient._
import zio._
import zio.interop.catz.monadErrorInstance
import io.circe.refined._

package object authorization {
  type AuthorizationModule = Has[AuthorizationModule.Service]
  object AuthorizationModule {
    def requestToken(req: AccessTokenRequest): RIO[AuthorizationModule, AccessTokenResponse] =
      ZIO.accessM(_.get.requestToken(req))

    def refreshToken(req: RefreshTokenRequest): RIO[AuthorizationModule, RefreshTokenResponse] =
      ZIO.accessM(_.get.refreshToken(req))

    val layer: TaskLayer[AuthorizationModule] = {
      val l1 = ZLayer.fromServices[AuthorizationConfig, H4sClient, AuthorizationModule.Service] {
        (config, httpClient) => new H4sAuthorizationService(config.apiTokenUri, httpClient)
      }

      (AuthorizationConfigModule.layer ++ HttpClientModule.layer) >>> l1
    }

    trait Service {
      def requestToken(req: AccessTokenRequest): Task[AccessTokenResponse]
      def refreshToken(req: RefreshTokenRequest): Task[RefreshTokenResponse]
    }
  }

  final private class H4sAuthorizationService(apiTokenUri: ApiTokenUri, httpClient: H4sClient)
      extends AuthorizationModule.Service {

    import H4sClientDsl._

    override def requestToken(req: AccessTokenRequest): Task[AccessTokenResponse] = {
      val params: ParamMap = Map(
        "grant_type"   -> req.grant_type,
        "code"         -> req.code,
        "redirect_uri" -> encode(req.redirect_uri.show)
      )

      val headers = Map(
        "Authorization" -> show"Basic ${base64Credentials(req.client_id, req.client_secret)}",
        "Content-Type"  -> "application/x-www-form-urlencoded; charset=UTF-8"
      )

      // I hope this is the only request that will need to use
      // Java as a secret weapon, due to the redirect_uri
      jPost(apiTokenUri.show, makeQueryString(params), headers)
        .map(jawn.decode[AccessTokenResponse])
        .flatMap(Task.fromEither(_))
    }

    override def refreshToken(req: RefreshTokenRequest): Task[RefreshTokenResponse] = {
      val urlForm = UrlForm(
        "grant_type"    -> req.grant_type,
        "refresh_token" -> req.refresh_token.show
      )

      val post = POST(
        urlForm,
        _: Uri,
        authorizationBasicHeader(req.client_id, req.client_secret),
        Accept(MediaType.application.json)
      )

      Task
        .fromEither(Uri.fromString(apiTokenUri.show))
        .flatMap(doRequest[RefreshTokenResponse](httpClient, _)(post))
    }
  }
}
