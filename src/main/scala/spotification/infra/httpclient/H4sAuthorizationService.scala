package spotification.infra.httpclient

import cats.implicits._
import io.circe.generic.auto._
import io.circe.jawn
import org.http4s.Method._
import org.http4s.{MediaRange, Uri, UrlForm}
import spotification.domain.spotify.authorization._
import zio.Task
import zio.interop.catz._
import eu.timepit.refined.auto._
import HttpClient._
import AuthorizationHttpClient._
import org.http4s.headers.Accept
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

// FEntityDecoder is messing with RefreshTokenResponse decoding somehow
import spotification.infra.Json.Implicits.{FEntityDecoder => _, _}

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

    // I hope this is the only request that will need to use
    // Java as a secret weapon, due to the redirect_uri
    jPost(apiTokenUri.show, makeQueryString(params), headers)
      .map(jawn.decode[AccessTokenResponse])
      .flatMap(Task.fromEither(_))
  }

  override def refreshToken(req: RefreshTokenRequest): Task[RefreshTokenResponse] = {
    val urlForm = UrlForm(
      "grant_type"    -> req.grant_type,
      "refresh_token" -> req.refresh_token.show
    )

    val post = POST.apply(
      urlForm,
      Uri.unsafeFromString(apiTokenUri.show),
      authorizationBasicHeader(req.client_id, req.client_secret),
      Accept(MediaRange.`*/*`)
    )

    httpClient
      .expect[String](post)
      .map(jawn.decode[RefreshTokenResponse])
      .flatMap(Task.fromEither(_))
  }
}
