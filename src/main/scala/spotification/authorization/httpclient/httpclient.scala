package spotification.authorization

import cats.syntax.show._
import eu.timepit.refined.auto._
import io.circe.jawn
import org.http4s.AuthScheme.{Basic, Bearer}
import org.http4s.Credentials.Token
import org.http4s.Method.POST
import org.http4s.headers.Accept
import org.http4s.{MediaType, Uri, UrlForm}
import spotification.authorization.json.implicits.{AccessTokenResponseDecoder, RefreshTokenResponseDecoder}
import spotification.authorization.service.{
  RefreshTokenService,
  RefreshTokenServiceEnv,
  RequestTokenService,
  RequestTokenServiceEnv
}
import spotification.common.httpclient.{H4sClient, HttpClientEnv, doRequest, jPost}
import spotification.common.json.implicits.ErrorResponseDecoder
import spotification.common.{ParamMap, encodeUrl, makeQueryString}
import spotification.config.AuthorizationConfig
import spotification.config.service.AuthorizationConfigEnv
import spotification.effect.leftStringEitherToTask
import zio.interop.catz.monadErrorInstance
import zio.{Has, Task, URLayer, ZLayer}

package object httpclient {
  import H4sClient.Dsl._

  type H4sAuthorization = org.http4s.headers.Authorization
  val H4sAuthorization: org.http4s.headers.Authorization.type = org.http4s.headers.Authorization

  val RequestTokenServiceLayer: URLayer[AuthorizationConfigEnv with HttpClientEnv, RequestTokenServiceEnv] =
    ContextLayer >>> ZLayer.fromService[Context, RequestTokenService](ctx => requestToken(ctx, _))

  val RefreshTokenServiceLayer: URLayer[AuthorizationConfigEnv with HttpClientEnv, RefreshTokenServiceEnv] =
    ContextLayer >>> ZLayer.fromService[Context, RefreshTokenService](ctx => refreshToken(ctx, _))

  def authorizationBasicHeader(clientId: ClientId, clientSecret: ClientSecret): H4sAuthorization =
    H4sAuthorization(Token(Basic, base64Credentials(clientId, clientSecret)))

  def authorizationBearerHeader(accessToken: AccessToken): H4sAuthorization =
    H4sAuthorization(Token(Bearer, accessToken.value))

  def makeAuthorizeH4sUri(authorizeUri: AuthorizeUri, req: AuthorizeRequest): Task[Uri] =
    leftStringEitherToTask(makeAuthorizeUri(authorizeUri, req))
      .map(uriString => Uri.unsafeFromString(uriString))

  private def requestToken(ctx: Context, req: AccessTokenRequest): Task[AccessTokenResponse] = {
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
    jPost(ctx.apiTokenUri.show, makeQueryString(params), headers)
      .map(jawn.decode[AccessTokenResponse])
      .flatMap(Task.fromEither(_))
  }

  private def refreshToken(ctx: Context, req: RefreshTokenRequest): Task[RefreshTokenResponse] = {
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
      .fromEither(Uri.fromString(ctx.apiTokenUri.show))
      .flatMap(doRequest[RefreshTokenResponse](ctx.httpClient, _)(post))
  }

  private final case class Context(apiTokenUri: ApiTokenUri, httpClient: H4sClient)
  private lazy val ContextLayer: URLayer[AuthorizationConfigEnv with HttpClientEnv, Has[Context]] =
    ZLayer.fromServices[AuthorizationConfig, H4sClient, Context] { (config, httpClient) =>
      Context(config.apiTokenUri, httpClient)
    }
}
