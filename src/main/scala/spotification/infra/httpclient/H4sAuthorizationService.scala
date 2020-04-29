package spotification.infra.httpclient

import io.circe.generic.auto._
import io.circe.{Decoder, jawn}
import org.http4s.Method._
import org.http4s.{Uri, UrlForm}
import spotification.core.spotify.authorization._
import zio.{RIO, Task}
import zio.interop.catz._
import HttpClient._
import AuthorizationHttpClient._
import spotification.core.CoreModule.BaseEnv
import spotification.core.spotify.authorization.AuthorizationModule.AuthorizationServiceEnv

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

  // ==========
  // IntelliJ says that No implicits where found for ToMapAux in `toParams(_)` function,
  // but it works fine on sbt, so don't panic!
  // ==========

  override def requestToken(req: AccessTokenRequest): RIO[AuthorizationServiceEnv, AccessTokenResponse] = {
    val params = toParams(req)
    apiTokenRequest[AccessTokenResponse](params, req.client_id, req.client_secret)
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
  ): RIO[BaseEnv, B] = {
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
