package spotification.infra.httpclient

import io.circe.generic.auto._
import io.circe.{jawn, Decoder}
import org.http4s.Method._
import org.http4s.implicits._
import org.http4s.{Uri, UrlForm}
import spotification.domain.spotify.authorization._
import spotification.infra.httpclient.authorization._
import zio.Task
import zio.interop.catz._
import cats.implicits._

// ==========
// Despite IntelliJ telling that `import io.circe.refined._` is not being used,
// it is required to make Circe work with Refined Types
// ==========
import io.circe.refined._

final class H4sAuthorizationService(httpClient: H4sClient) extends Authorization.Service with H4sClientDsl {
  private val accountsUri: Uri = uri"https://accounts.spotify.com"
  private val authorizeUri: Uri = accountsUri.withPath("/authorize")
  private val apiTokenUri: Uri = accountsUri.withPath("/api/token")

  // ==========
  // IntelliJ says that No implicits where found for ToMapAux in `toParams(_)` function,
  // but it works fine on sbt, so don't panic!
  // ==========

  // There is an `AuthorizeResponse`, but it will be sent through the `redirect_uri`,
  // (the "callback" of the authorization process). So here we just return Unit
  override def authorize(req: AuthorizeRequest): Task[Unit] = {
    val params = toParams(req)

    // `scope` can't be generically built due to the required
    // List[Scope] => SpaceSeparatedString transformation
    val params2 = req.scope
      .map(addScopeParam(params, _))
      .map(_.leftMap(new Exception(_)))
      .map(Task.fromEither(_))
      .getOrElse(Task(params))

    params2.flatMap(p2 => httpClient.expect[Unit](authorizeUri.withQueryParams(p2)))
  }

  override def requestToken(req: AccessTokenRequest): Task[AccessTokenResponse] = {
    val params = toParams(req)
    apiTokenRequest[AccessTokenResponse](params, req.client_id, req.client_secret)
  }

  @SuppressWarnings(Array("org.wartremover.warts.Product"))
  override def refreshToken(req: RefreshTokenRequest): Task[RefreshTokenResponse] = {
    val params = toParams(req)
    apiTokenRequest[RefreshTokenResponse](params, req.client_id, req.client_secret)
  }

  private def apiTokenRequest[B: Decoder](
    params: ParamMap,
    clientId: ClientId,
    clientSecret: ClientSecret
  ): Task[B] = {
    val post = POST(UrlForm(params.toSeq: _*), apiTokenUri, authorizationBasicHeader(clientId, clientSecret))

    httpClient
      .expect[String](post)
      .flatMap(s => Task.fromEither(jawn.decode[B](s)))
  }
}
