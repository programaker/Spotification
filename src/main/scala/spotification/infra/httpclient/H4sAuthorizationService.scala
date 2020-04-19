package spotification.infra.httpclient

import io.circe.generic.auto._
import io.circe.{jawn, Decoder}
import org.http4s.Method._
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.implicits._
import org.http4s.{Uri, UrlForm}
import spotification.domain.scope._
import spotification.domain.spotify.authorization._
import zio.Task
import zio.interop.catz._

// ==========
// Despite IntelliJ telling that `import io.circe.refined._` is not being used,
// it is required to make Circe work with Refined Types
// ==========
import io.circe.refined._

final class H4sAuthorizationService(httpClient: H4sClient) extends AuthorizationService {
  private val accountsUri: Uri = uri"https://accounts.spotify.com"
  private val authorizeUri: Uri = accountsUri.withPath("/authorize")
  private val apiTokenUri: Uri = accountsUri.withPath("/api/token")

  private val dsl: Http4sClientDsl[Task] = new Http4sClientDsl[Task] {}
  import dsl._

  // ==========
  // IntelliJ says that No implicits where found for ToMapAux in `toParams(_)` function,
  // but it works fine on sbt, so don't panic!
  // ==========

  // There is an `AuthorizeResponse`, but it will be sent through the `redirect_uri`,
  // (the "callback" of the authorization process). So here we just return Unit
  override def authorize(req: AuthorizeRequest): Task[Unit] = {
    val params = toParams(req)

    // `scope` can't be generically build due to the required List[Scope] => String transformation
    val paramsWithScope = req.scope.fold(params)(s => params + ("scope" -> joinScopes(s)))

    httpClient.expect[Unit](authorizeUri.withQueryParams(paramsWithScope))
  }

  override def requestToken(req: AccessTokenRequest): Task[AccessTokenResponse] = {
    val params = toParams(req)
    apiTokenRequest[AccessTokenResponse](params, req.client_id, req.client_secret)
  }

  override def refreshToken(req: RefreshTokenRequest): Task[RefreshTokenResponse] = {
    val params = toParams(req)
    apiTokenRequest[RefreshTokenResponse](params, req.client_id, req.client_secret)
  }

  private def apiTokenRequest[B: Decoder](
    params: Map[String, String],
    clientId: ClientId,
    clientSecret: ClientSecret
  ): Task[B] = {
    val post = POST(UrlForm(params.toSeq: _*), apiTokenUri, authorizationBasicHeader(clientId, clientSecret))

    httpClient
      .expect[String](post)
      .flatMap(s => Task.fromEither(jawn.decode[B](s)))
  }
}
