package spotification.common

import cats.syntax.either._
import cats.syntax.show._
import eu.timepit.refined.auto._
import io.circe.{Decoder, jawn}
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.client.middleware.Logger
import org.http4s.{Request, Uri}
import spotification.concurrent.ExecutionContextR
import spotification.config.ClientConfig
import spotification.config.service.ClientConfigR
import zio._
import zio.interop.catz.{catsIOResourceSyntax, taskConcurrentInstance, taskEffectInstance}

import java.net.URI
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers
import java.net.http.{HttpClient, HttpRequest}
import java.nio.charset.StandardCharsets.UTF_8
import scala.concurrent.ExecutionContext

package object httpclient {
  type H4sClient = Client[Task]
  object H4sClient {
    val Dsl: Http4sClientDsl[Task] = new Http4sClientDsl[Task] {}
  }

  type HttpClientR = Has[H4sClient]

  val HttpClientLayer: RLayer[ExecutionContextR with ClientConfigR, HttpClientR] = {
    def makeHttpClient: URIO[ExecutionContext, RManaged[ExecutionContext, H4sClient]] =
      ZIO.runtime[ExecutionContext].map(implicit rt => BlazeClientBuilder[Task](rt.environment).resource.toManaged)

    def addLogger(config: ClientConfig): H4sClient => H4sClient =
      Logger(config.logHeaders, config.logBody)(_)

    ZLayer.fromServicesManaged[ExecutionContext, ClientConfig, Any, Throwable, H4sClient] { (ex, config) =>
      makeHttpClient.toManaged_.flatten.map(addLogger(config)).provide(ex)
    }
  }

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

  /**
   * So, why did we need to appeal to Java HttpClient!?
   * The problem is this issue in Http4s https://github.com/http4s/http4s/issues/2445
   * The way they choose to encode uri's is not accepted by Spotify.
   *
   * We have managed to found a workaround for the 1st step using Uri.unsafeFromString("free-style String"),
   * but no similar workaround existed for UrlForm.
   *
   * PS - I'm impressed about how simple the new Java HttpClient is!
   * It looks like a library
   */
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

        val builder2 = headers.foldLeft(builder) { case (builder, (key, value)) =>
          builder.header(key, value)
        }

        builder2.build()
      }

      client.send(request, BodyHandlers.ofString()).body()
    }

  def eitherUriStringToH4s(eitherUriString: Either[String, UriString]): Either[Throwable, Uri] =
    eitherUriString
      .leftMap(new Exception(_))
      .flatMap((s: UriString) => Uri.fromString(s))
}
