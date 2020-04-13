package spotification.spotify.authorization

import org.http4s.Uri
import org.http4s.implicits._
import org.http4s.client._
import spotification.spotify._
import zio.Task
import zio.interop.catz._

final class LiveAuthorizationService(credentials: Credentials, httpClient: Client[Task]) extends AuthorizationService {
  private val authorizeUrl: Uri = uri"https://accounts.spotify.com/authorize"
  private val apiTokenUrl: Uri = uri"https://accounts.spotify.com/api/token"

  override def authorize(req: AuthorizationRequest): Task[Unit] =
    Task(authorizeUrl)
      .map(_.withQueryParams(toQueryStringParams(req)))
      .map(httpClient.expect[Unit](_))

  override def requestToken(req: TokenRequest): Task[TokenResponse] = ???

  override def refreshToken(req: RefreshTokenRequest): Task[RefreshTokenResponse] = ???
}
