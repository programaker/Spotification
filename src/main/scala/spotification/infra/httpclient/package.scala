package spotification.infra

import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers
import java.net.http.{HttpClient, HttpRequest}
import java.net.{URI, URLEncoder}
import java.nio.charset.StandardCharsets.UTF_8

import cats.implicits._
import eu.timepit.refined.auto._
import eu.timepit.refined.cats._
import io.circe.{Decoder, jawn}
import org.http4s.AuthScheme.{Basic, Bearer}
import org.http4s.Credentials.Token
import org.http4s.{Request, Uri}
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.client.middleware.Logger
import spotification.domain.config.ClientConfig
import spotification.domain.spotify.ErrorResponse
import spotification.domain.spotify.authorization.Scope.joinScopes
import spotification.domain.spotify.authorization._
import spotification.infra.concurrent.ExecutionContextModule
import spotification.infra.config.ClientConfigModule
import zio._
import zio.interop.catz._

import scala.concurrent.ExecutionContext

package object httpclient {
  type H4sClient = Client[Task]
  type H4sAuthorization = org.http4s.headers.Authorization
  type ParamMap = Map[String, String]

  val H4sClientDsl: Http4sClientDsl[Task] = new Http4sClientDsl[Task] {}
  val H4sAuthorization: org.http4s.headers.Authorization.type = org.http4s.headers.Authorization

  type HttpClientModule = Has[H4sClient]
  object HttpClientModule {
    val layer: TaskLayer[HttpClientModule] = {
      val makeHttpClient: URIO[ExecutionContext, RManaged[ExecutionContext, Client[Task]]] =
        ZIO.runtime[ExecutionContext].map(implicit rt => BlazeClientBuilder[Task](rt.environment).resource.toManaged)

      def addLogger(config: ClientConfig): Client[Task] => Client[Task] =
        Logger(config.logHeaders, config.logBody)(_)

      val l = ZLayer.fromServicesManaged[ExecutionContext, ClientConfig, Any, Throwable, H4sClient] { (ex, config) =>
        makeHttpClient.toManaged_.flatten.map(addLogger(config)).provide(ex)
      }

      (ExecutionContextModule.layer ++ ClientConfigModule.layer) >>> l
    }
  }

  // HTTP4s Uri should be able to encode query params, but in my tests
  // URIs are not properly encoded:
  //
  // uri"https://foo.com".withQueryParam("redirect_uri", "https://bar.com")
  // > org.http4s.Uri = https://foo.com?redirect_uri=https%3A//bar.com <- did not encode `//`
  //
  // URLEncoder.encode("https://bar.com", UTF_8.toString)
  // > String = https%3A%2F%2Fbar.com <- encoded `//` correctly
  val encode: String => String =
    URLEncoder.encode(_, UTF_8)

  val makeQueryString: ParamMap => String =
    _.map { case (k, v) => show"$k=$v" } mkString "&"

  def addScopeParam(params: ParamMap, scopes: List[Scope]): Either[String, ParamMap] =
    joinScopes(scopes).map(s => params + ("scope" -> encode(s)))

  def doRequest[A: Decoder](httpClient: H4sClient, uri: Uri)(
    req: Uri => Task[Request[Task]]
  )(implicit der: Decoder[ErrorResponse]): Task[A] =
    req(uri)
      .flatMap(httpClient.expect[String])
      .map(s => jawn.decode[A](s).leftMap(_ => jawn.decode[ErrorResponse](s)))
      .map(_.leftMap {
        case Left(decodeError) =>
          decodeError
        case Right(ErrorResponse(status, message)) =>
          new Exception(show"Error: status=$status, message='$message', uri='${uri.renderString}'")
      })
      .flatMap(Task.fromEither(_))

  /** So, why did we need to appeal to Java HttpClient!?
   * The problem is this issue in Http4s https://github.com/http4s/http4s/issues/2445
   * The way they choose to encode uri's is not accepted by Spotify.
   *
   * We have managed to found a workaround for the 1st step using Uri(path = "free-style String"),
   * but no similar workaround existed for UrlForm.
   *
   * PS - I'm impressed about how simple the new Java HttpClient is!
   * It looks like a library */
  def jPost(uri: String, body: String, headers: Map[String, String]): Task[String] =
    Task {
      val client = HttpClient
        .newBuilder()
        .version(HttpClient.Version.HTTP_2)
        .build()

      val request = {
        val builder = HttpRequest
          .newBuilder(URI.create(uri))
          .POST(BodyPublishers.ofString(body, UTF_8))

        val builder2 = headers.foldLeft(builder) {
          case (builder, (key, value)) => builder.header(key, value)
        }

        builder2.build()
      }

      client.send(request, BodyHandlers.ofString()).body()
    }

  def authorizationBasicHeader(clientId: ClientId, clientSecret: ClientSecret): H4sAuthorization =
    H4sAuthorization(Token(Basic, base64Credentials(clientId, clientSecret)))

  def authorizationBearerHeader(accessToken: AccessToken): H4sAuthorization =
    H4sAuthorization(Token(Bearer, accessToken.value))

  def makeAuthorizeUri(authorizeUri: AuthorizeUri, req: AuthorizeRequest): Task[Uri] = {
    // required params
    val params: ParamMap = Map(
      "client_id"     -> req.client_id.show,
      "response_type" -> req.response_type,
      "redirect_uri"  -> encode(req.redirect_uri.show)
    )

    // optional params
    val params2 = req.show_dialog
      .fold(params)(b => params + ("show_dialog" -> b.show))
    val params3 = req.state
      .fold(params2)(s => params2 + ("state" -> s.show))

    // decoding optional scope
    val params4 = req.scope
      .map(addScopeParam(params3, _))
      .map(_.leftMap(new Exception(_)))
      .map(Task.fromEither(_))
      .getOrElse(Task(params3))

    // https://github.com/http4s/http4s/issues/2445
    // I did this for the same reason I've created `HttpClient.encode` function
    // which is the encoding choice of http4s not being accepted by Spotify.
    // Adding everything to `path` bypasses their encoding.
    params4
      .map(makeQueryString)
      .map(q => Uri(path = show"$authorizeUri?$q"))
  }
}
