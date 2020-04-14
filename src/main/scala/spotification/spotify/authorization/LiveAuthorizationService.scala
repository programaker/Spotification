package spotification.spotify.authorization

import org.http4s.Uri
import org.http4s.client._
import org.http4s.implicits._
import spotification.spotify._
import zio.Task
import zio.interop.catz._

final class LiveAuthorizationService(credentials: Credentials, httpClient: Client[Task]) extends AuthorizationService {
  private val accountsUri: Uri = uri"https://accounts.spotify.com"
  private val authorizeUri: Uri = accountsUri.withPath("/authorize")
  private val apiTokenUri: Uri = accountsUri.withPath("/api/token")

  override def authorize(req: AuthorizeRequest): Task[Unit] = {
    val params = toParams(req)
    httpClient.expect[Unit](authorizeUri.withQueryParams(params))
  }

  override def requestToken(req: AccessTokenRequest): Task[AccessTokenResponse] = ???

  override def refreshToken(req: RefreshTokenRequest): Task[RefreshTokenResponse] = ???
}
