package spotification.infra.httpclient

import cats.implicits._
import io.circe.generic.auto._
import io.circe.{Decoder, jawn}
import org.http4s.Method._
import org.http4s.implicits._
import org.http4s.{MediaType, Uri, UrlForm}
import spotification.core.spotify.authorization._
import zio.Task
import zio.interop.catz._
import HttpClient._
import org.http4s.headers.Accept

// ==========
// Despite IntelliJ telling that
// `import io.circe.refined._`
// `import spotification.infra.json._`
// are not being used, they are required to compile
// ==========
import io.circe.refined._
import spotification.infra.Json._

final class H4sAuthorizationService(httpClient: H4sClient) extends AuthorizationModule.Service {
  private val accountsUri: Uri = uri"https://accounts.spotify.com"
  private val authorizeUri: Uri = accountsUri.withPath("/authorize")
  private val apiTokenUri: Uri = accountsUri.withPath("/api/token")

  import H4sTaskClientDsl._

  // ==========
  // IntelliJ says that No implicits where found for ToMapAux in `toParams(_)` function,
  // but it works fine on sbt, so don't panic!
  // ==========

  // (the "callback" of the authorization process). So here we just return Unit
  override def authorize(req: AuthorizeRequest): Task[String] = {
    val params = toParams(req)

    // `scope` can't be generically built due to the required
    // List[Scope] => SpaceSeparatedString transformation
    val params2 = req.scope
      .map(addScopeParam(params, _))
      .map(_.leftMap(new Exception(_)))
      .map(Task.fromEither(_))
      .getOrElse(Task(params))

    params2.flatMap(p2 =>
      httpClient.expect[String](GET(authorizeUri.withQueryParams(p2), Accept(MediaType.text.plain)))
    )
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
