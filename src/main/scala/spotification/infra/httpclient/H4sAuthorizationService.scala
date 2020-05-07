package spotification.infra.httpclient

import io.circe.generic.auto._
import io.circe.{Decoder, jawn}
import org.http4s.Method._
import org.http4s.{Uri, UrlForm}
import spotification.domain.spotify.authorization._
import zio.{RIO, Task}
import zio.interop.catz._
import HttpClient._
import AuthorizationHttpClient._
import spotification.application.AuthorizationModule
import spotification.application.AuthorizationModule.AuthorizationServiceEnv
import spotification.domain.spotify.authorization.Authorization.base64Credentials
import spotification.infra.httpclient.JHttpClient.jPost

// ==========
// Despite IntelliJ telling that
// `import io.circe.refined._`
// `import spotification.infra.json._`
// are not being used, they are required to compile
// ==========
import io.circe.refined._
import spotification.infra.Json._

final class H4sAuthorizationService(httpClient: H4sClient) extends AuthorizationModule.Service {
  import H4sTaskClientDsl._

  override def requestToken(req: AccessTokenRequest): RIO[AuthorizationServiceEnv, AccessTokenResponse] = {
    val params = toParams(req)

    val headers = Map(
      "Authorization" -> s"Basic ${base64Credentials(req.client_id, req.client_secret)}",
      "Content-Type"  -> "application/x-www-form-urlencoded; charset=UTF-8"
    )

    // I hope this is the only request that will need to use
    // Java as a secret weapon, due to the redirect_uri
    jPost[AuthorizationServiceEnv](apiTokenUri, makeQueryString(params), headers)
      .flatMap(s => Task.fromEither(jawn.decode[AccessTokenResponse](s)))
  }

  @SuppressWarnings(Array("org.wartremover.warts.Product"))
  override def refreshToken(req: RefreshTokenRequest): RIO[AuthorizationServiceEnv, RefreshTokenResponse] = {
    val params = toParams(req)
    apiTokenRequest[RefreshTokenResponse](params, req.client_id, req.client_secret)
  }

  private def apiTokenRequest[B: Decoder](
    params: ParamMap,
    clientId: ClientId,
    clientSecret: ClientSecret
  ): RIO[AuthorizationServiceEnv, B] = {
    val post = POST(
      UrlForm(params.toSeq: _*),
      Uri.unsafeFromString(apiTokenUri),
      authorizationBasicHeader(clientId, clientSecret)
    )

    httpClient
      .expect[String](post)
      .flatMap(s => Task.fromEither(jawn.decode[B](s)))
  }
}
