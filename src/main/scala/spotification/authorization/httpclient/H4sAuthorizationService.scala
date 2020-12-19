package spotification.authorization.httpclient

import cats.syntax.show._
import eu.timepit.refined.auto._
import io.circe.jawn
import org.http4s.Method.POST
import org.http4s.headers.Accept
import org.http4s.{MediaType, Uri, UrlForm}
import spotification.authorization._
import spotification.authorization.json.implicits.{AccessTokenResponseDecoder, RefreshTokenResponseDecoder}
import spotification.authorization.service.AuthorizationService
import spotification.common.httpclient.{H4sClient, doRequest, jPost}
import spotification.common.json.implicits.ErrorResponseDecoder
import spotification.common.{ParamMap, encodeUrl, makeQueryString}
import zio.Task
import zio.interop.catz.monadErrorInstance

final class H4sAuthorizationService(apiTokenUri: ApiTokenUri, httpClient: H4sClient) extends AuthorizationService {
  import H4sClient.Dsl._

  override def requestToken(req: AccessTokenRequest): Task[AccessTokenResponse] = {
    val params: ParamMap = Map(
      "grant_type"   -> Some(req.grant_type),
      "code"         -> Some(req.code),
      "redirect_uri" -> Some(encodeUrl(req.redirect_uri.show))
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

    val post = POST(
      urlForm,
      _: Uri,
      authorizationBasicHeader(req.client_id, req.client_secret),
      Accept(MediaType.application.json)
    )

    Task
      .fromEither(Uri.fromString(apiTokenUri.show))
      .flatMap(doRequest[RefreshTokenResponse](httpClient, _)(post))
  }
}
