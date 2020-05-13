package spotification.infra.httpclient

import java.net.URI
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers
import java.net.http.{HttpClient, HttpRequest}
import java.nio.charset.StandardCharsets.UTF_8

import zio.Task

/** So, why did we need to appeal to Java HttpClient!?
 * The problem is this issue in Http4s https://github.com/http4s/http4s/issues/2445
 * The way they choose to encode uri's is not accepted by Spotify.
 *
 * We have managed to found a workaround for the 1st step using Uri(path = "free-style String"),
 * but no similar workaround existed for UrlForm.
 *
 * PS - I'm impressed about how simple the new Java HttpClient is!
 * It looks like a library */
object JHttpClient {
  def jPost(uri: String, body: String, headers: Map[String, String]): Task[String] = Task {
    val client = HttpClient
      .newBuilder()
      .version(HttpClient.Version.HTTP_2)
      .build()

    val request = {
      val builder = HttpRequest
        .newBuilder(URI.create(uri))
        .POST(BodyPublishers.ofString(body, UTF_8))

      val builder2 = headers.foldLeft(builder) {
        case (builder, (key, value)) =>
          builder.header(key, value)
      }

      builder2.build()
    }

    client.send(request, BodyHandlers.ofString()).body()
  }
}
