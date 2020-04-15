package spotification.spotify.authorization

import org.http4s.Method._
import org.http4s.client._
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.headers.{Authorization => H4sAuthorization}
import org.http4s.implicits._
import org.http4s.{AuthScheme, Uri, UrlForm, Credentials => H4sCredentials}
import spotification.spotify._
import zio.Task
import zio.interop.catz._
import io.circe.generic.auto._
import io.circe.jawn

//Despite IntelliJ telling that `import io.circe.refined._` is not being used,
//it is required to make Circe work with Refined Types
import io.circe.refined._

final class LiveAuthorizationService(credentials: Credentials, httpClient: Client[Task]) extends AuthorizationService {
  private val accountsUri: Uri = uri"https://accounts.spotify.com"
  private val authorizeUri: Uri = accountsUri.withPath("/authorize")
  private val apiTokenUri: Uri = accountsUri.withPath("/api/token")

  private val dsl: Http4sClientDsl[Task] = new Http4sClientDsl[Task] {}
  import dsl._

  override def authorize(req: AuthorizeRequest): Task[Unit] = {
    val params = toParams(req)
    httpClient.expect[Unit](authorizeUri.withQueryParams(params))
  }

  override def requestToken(req: AccessTokenRequest): Task[AccessTokenResponse] = {
    val params = toParams(req)

    val urlForm = UrlForm(params.toSeq: _*)
    val basic = base64Credentials(req.credentials)
    val header = H4sAuthorization(H4sCredentials.Token(AuthScheme.Basic, basic))
    val post = POST(urlForm, apiTokenUri, header)

    httpClient
      .expect[String](post)
      .flatMap(s => Task.fromEither(jawn.decode[AccessTokenResponse](s)))
  }

  override def refreshToken(req: RefreshTokenRequest): Task[RefreshTokenResponse] = ???
}
