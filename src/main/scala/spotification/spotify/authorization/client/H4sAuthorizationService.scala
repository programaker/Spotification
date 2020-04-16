package spotification.spotify.authorization.client

import io.circe.generic.auto._
import io.circe.{jawn, Decoder}
import org.http4s.Method._
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.implicits._
import org.http4s.{Uri, UrlForm}
import spotification.spotify._
import spotification.spotify.authorization._
import spotification.spotify.authorization.module.AuthorizationService
import zio.Task
import zio.interop.catz._

// ==========
// Despite IntelliJ telling that `import io.circe.refined._` is not being used,
// it is required to make Circe work with Refined Types
// ==========
import io.circe.refined._

final class H4sAuthorizationService(httpClient: HttpClient) extends AuthorizationService {
  private val accountsUri: Uri = uri"https://accounts.spotify.com"
  private val authorizeUri: Uri = accountsUri.withPath("/authorize")
  private val apiTokenUri: Uri = accountsUri.withPath("/api/token")

  private val dsl: Http4sClientDsl[Task] = new Http4sClientDsl[Task] {}
  import dsl._

  // ==========
  // IntelliJ says that No implicits where found for `toParams(_)` function,
  // but it works fine on sbt, so don't panic!
  // ==========

  // There is an `AuthorizeResponse`, but it will be sent through the `redirect_uri`,
  // (the "callback" of the authorization process). So here we just return Unit
  override def authorize(req: AuthorizeRequest): Task[Unit] = {
    val params = toParams(req)
    httpClient.expect[Unit](authorizeUri.withQueryParams(params))
  }

  override def requestToken(req: AccessTokenRequest): Task[AccessTokenResponse] =
    apiTokenRequest[AccessTokenRequest, AccessTokenResponse](req, req.credentials)

  override def refreshToken(req: RefreshTokenRequest): Task[RefreshTokenResponse] =
    apiTokenRequest[RefreshTokenRequest, RefreshTokenResponse](req, req.credentials)

  private def apiTokenRequest[A <: Product, B](
    req: A,
    credentials: Credentials
  )(implicit m: ToMapAux[A], d: Decoder[B]): Task[B] = {
    val params = toParams(req).toSeq
    val post = POST(UrlForm(params: _*), apiTokenUri, authorizationBasicHeader(credentials))

    httpClient
      .expect[String](post)
      .flatMap(s => Task.fromEither(jawn.decode[B](s)))
  }
}
