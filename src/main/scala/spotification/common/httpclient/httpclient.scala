package spotification.common

import cats.syntax.either._
import cats.syntax.show._
import eu.timepit.refined.auto._
import io.circe.{Decoder, jawn}
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.client.middleware.Logger
import org.http4s.{EntityDecoder, Request, Uri}
import spotification.config.ClientConfig
import spotification.config.service.ClientConfigR
import spotification.effect.{managedTaskDispatcher, managedZIORuntime}
import zio.blocking.Blocking
import zio.clock.Clock
import zio.interop.catz.{asyncRuntimeInstance, catsIOResourceSyntax}
import zio.{Has, RLayer, Task, ZLayer}

import java.net.URI
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers
import java.net.http.{HttpClient, HttpRequest}
import java.nio.charset.StandardCharsets.UTF_8

package object httpclient {
  type H4sClient = Client[Task]
  object H4sClient {
    val Dsl: Http4sClientDsl[Task] = new Http4sClientDsl[Task] {}
  }

  type HttpClientR = Has[H4sClient]

  val HttpClientLayer: RLayer[ClientConfigR with Clock with Blocking, HttpClientR] =
    ZLayer.fromServiceManaged[ClientConfig, Clock with Blocking, Throwable, H4sClient] { config =>
      managedZIORuntime[Clock with Blocking].flatMap { implicit rt =>
        managedTaskDispatcher.flatMap { implicit dispatcher =>
          val addLogger = Logger(config.logHeaders, config.logBody)(_: H4sClient)
          BlazeClientBuilder[Task].resource.map(addLogger).toManaged
        }
      }
    }

  def doRequest[A: Decoder](httpClient: H4sClient, h4sUri: Either[Throwable, Uri])(
    req: Uri => Request[Task]
  )(implicit der: Decoder[ErrorResponse], ed: EntityDecoder[Task, String]): Task[A] =
    Task.fromEither(h4sUri).flatMap(requestUri[A](httpClient, _)(req))

  /**
   * So, why did we need to appeal to Java HttpClient!? The problem is this issue in Http4s
   * https://github.com/http4s/http4s/issues/2445 The way they choose to encode uri's is not accepted by Spotify.
   *
   * We have managed to found a workaround for the 1st step using Uri.unsafeFromString("free-style String"), but no
   * similar workaround existed for UrlForm.
   *
   * PS - I'm impressed about how simple the new Java HttpClient is! It looks like a library
   */
  def jPost(uri: String, body: String, headers: Map[String, String]): Task[String] = {
    val client = Task {
      HttpClient
        .newBuilder()
        .version(HttpClient.Version.HTTP_2)
        .build()
    }

    val request = Task {
      val builder = HttpRequest
        .newBuilder(URI.create(uri))
        .POST(BodyPublishers.ofString(body, UTF_8))

      val builder2 = headers.foldLeft(builder) { case (builder, (key, value)) =>
        builder.header(key, value)
      }

      builder2.build()
    }

    client
      .zipWithPar(request)((client, request) => client.sendAsync(request, BodyHandlers.ofString()))
      .flatMap(Task.fromCompletionStage(_))
      .map(_.body())
  }

  def uriStringToUri(uriString: UriString): Either[Throwable, Uri] =
    Uri.fromString(uriString)

  private def requestUri[A: Decoder](httpClient: H4sClient, uri: Uri)(
    req: Uri => Request[Task]
  )(implicit der: Decoder[ErrorResponse], ed: EntityDecoder[Task, String]): Task[A] =
    Task(req(uri))
      .flatMap(httpClient.expect[String])
      .map(s => jawn.decode[A](s).leftMap(_ => jawn.decode[ErrorResponse](s)))
      .map(_.leftMap {
        case Left(decodeError) =>
          decodeError
        case Right(ErrorResponse(status, message)) =>
          new Exception(show"Error: status=$status, message='$message', uri='${uri.renderString}'")
      })
      .flatMap(Task.fromEither(_))
}
