package spotification.infra.httpclient

import cats.implicits._
import io.circe.generic.auto._
import io.circe.jawn
import org.http4s.Method._
import org.http4s.{Uri, UrlForm}
import spotification.domain.spotify.authorization._
import zio.Task
import zio.interop.catz._
import eu.timepit.refined.auto._
import HttpClient._
import AuthorizationHttpClient._
import spotification.domain.spotify.authorization.Authorization.base64Credentials
import spotification.infra.httpclient.JHttpClient.jPost
import spotification.infra.spotify.authorization.AuthorizationModule

// ==========
// Despite IntelliJ telling that
// `import io.circe.refined._`
// `import spotification.infra.Json.Implicits._`
// are not being used, they are required to compile
// ==========
import io.circe.refined._
import spotification.infra.Json.Implicits._

final class H4sAuthorizationService(apiTokenUri: ApiTokenUri, httpClient: H4sClient)
    extends AuthorizationModule.Service {

  import H4sClientDsl._

  override def requestToken(req: AccessTokenRequest): Task[AccessTokenResponse] = {
    val params: ParamMap = Map(
      "grant_type"   -> req.grant_type,
      "code"         -> req.code,
      "redirect_uri" -> encode(req.redirect_uri.show)
    )

    val headers = Map(
      "Authorization" -> show"Basic ${base64Credentials(req.client_id, req.client_secret)}",
      "Content-Type"  -> "application/x-www-form-urlencoded; charset=UTF-8"
    )

    jPost(apiTokenUri.show, makeQueryString(params), headers)
      .map(jawn.decode[AccessTokenResponse])
      .flatMap(Task.fromEither(_))
  }

  override def refreshToken(req: RefreshTokenRequest): Task[RefreshTokenResponse] = {
    val params: ParamMap = Map(
      "grant_type"    -> req.grant_type,
      "refresh_token" -> req.refresh_token.show
    )

    val headers = Map(
      "Authorization" -> show"Basic ${base64Credentials(req.client_id, req.client_secret)}",
      "Content-Type"  -> "application/x-www-form-urlencoded; charset=UTF-8"
    )

    jPost(apiTokenUri.show, makeQueryString(params), headers)
      .map(jawn.decode[RefreshTokenResponse])
      .flatMap(Task.fromEither(_))
  }
}
