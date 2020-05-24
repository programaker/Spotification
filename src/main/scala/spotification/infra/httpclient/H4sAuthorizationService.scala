package spotification.infra.httpclient

import cats.implicits._
import io.circe.generic.auto._
import io.circe.jawn
import spotification.domain.spotify.authorization._
import zio.Task
import eu.timepit.refined.auto._
import HttpClient._
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

final class H4sAuthorizationService(apiTokenUri: ApiTokenUri) extends AuthorizationModule.Service {
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
