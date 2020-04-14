package spotification.spotify.authorization

import io.circe.Decoder
import org.http4s.Method._
import org.http4s.client._
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.headers.{Authorization => H4sAuthorization}
import org.http4s.implicits._
import org.http4s.{AuthScheme, EntityDecoder, Uri, UrlForm, Credentials => H4sCredentials}
import spotification.spotify._
import zio.Task
import zio.interop.catz._
import io.circe.generic.semiauto._
import org.http4s.circe._

final class LiveAuthorizationService(credentials: Credentials, httpClient: Client[Task]) extends AuthorizationService {
  private val accountsUri: Uri = uri"https://accounts.spotify.com"
  private val authorizeUri: Uri = accountsUri.withPath("/authorize")
  private val apiTokenUri: Uri = accountsUri.withPath("/api/token")

  import LiveAuthorizationService._
  import LiveAuthorizationService.dsl._

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

    httpClient.expect[AccessTokenResponse](post)
  }

  override def refreshToken(req: RefreshTokenRequest): Task[RefreshTokenResponse] = ???
}

object LiveAuthorizationService {
  implicit val accessTokenResponseD: Decoder[AccessTokenResponse] = deriveDecoder[AccessTokenResponse]
  implicit val accessTokenResponseED: EntityDecoder[Task, AccessTokenResponse] = jsonOf
  val dsl: Http4sClientDsl[Task] = new Http4sClientDsl[Task] {}
}
