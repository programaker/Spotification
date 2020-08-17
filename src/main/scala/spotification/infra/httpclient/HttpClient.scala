package spotification.infra.httpclient

import java.net.URLEncoder
import java.nio.charset.StandardCharsets.UTF_8

import cats.implicits._
import eu.timepit.refined.auto._
import io.circe.{Decoder, jawn}
import org.http4s.{Request, Uri}
import org.http4s.client.dsl.Http4sClientDsl
import spotification.domain.spotify.{CommonResponses, SpotifyResponse}
import spotification.domain.spotify.authorization.Scope
import spotification.domain.spotify.authorization.Scope.joinScopes
import zio.Task
import zio.interop.catz._

object HttpClient {
  val H4sClientDsl: Http4sClientDsl[Task] = new Http4sClientDsl[Task] {}

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

  def doRequest[A <: SpotifyResponse](httpClient: H4sClient, uri: Uri)(
    req: Uri => Task[Request[Task]]
  )(implicit
    D: Decoder[SpotifyResponse]
  ): Task[A] =
    req(uri)
      .flatMap(httpClient.expect[String])
      .map(jawn.decode[SpotifyResponse])
      .flatMap(Task.fromEither(_))
      .flatMap {
        case CommonResponses.Error(status, message) =>
          Task.fail(new Exception(show"Error: status=$status, message='$message', uri='${uri.renderString}'"))
        case a: A =>
          Task.succeed(a)
        case _ =>
          Task.fail(new Exception("Unexpected case. Check if the type params matches the URI"))
      }
}
