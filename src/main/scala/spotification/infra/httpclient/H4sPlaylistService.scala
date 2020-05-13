package spotification.infra.httpclient

import cats.implicits._
import io.circe.jawn
import io.circe.generic.auto._
import org.http4s.Method._
import org.http4s.{ParseFailure, Uri}
import spotification.domain.spotify.playlist.{PlaylistApiUri, PlaylistItemsRequest, PlaylistItemsResponse}
import spotification.infra.httpclient.AuthorizationHttpClient.authorizationBearerHeader
import spotification.infra.httpclient.HttpClient.H4sClientDsl
import spotification.infra.spotify.playlist.PlaylistModule
import zio.{IO, Task}
import zio.interop.catz._
import eu.timepit.refined.cats._
import eu.timepit.refined.auto._
import spotification.domain.spotify.playlist.PlaylistItemsRequest.{FirstRequest, NextRequest}

// ==========
// Despite IntelliJ telling that
// `import io.circe.refined._`
// `import spotification.infra.Json.Implicits._`
// are not being used, they are required to compile
// ==========
import io.circe.refined._
import spotification.infra.Json.Implicits._

final class H4sPlaylistService(playlistApiUri: PlaylistApiUri, httpClient: H4sClient) extends PlaylistModule.Service {
  import H4sClientDsl._

  override def getPlaylistItems(req: PlaylistItemsRequest): Task[PlaylistItemsResponse] = {
    val (accessToken, uri) = req match {
      case first: FirstRequest               => (first.accessToken, makeUri(first))
      case NextRequest(accessToken, nextUri) => (accessToken, Uri.fromString(nextUri))
    }

    IO.fromEither(uri)
      .absorbWith(parseFailure => new Exception(parseFailure.message))
      .map(GET(_, authorizationBearerHeader(accessToken)))
      .flatMap(httpClient.expect[String])
      .map(jawn.decode[PlaylistItemsResponse])
      .flatMap(Task.fromEither(_))
  }

  private def makeUri(req: FirstRequest): Either[ParseFailure, Uri] =
    Uri
      .fromString(show"$playlistApiUri/${req.playlistId}/tracks")
      .map {
        _.withQueryParam("fields", req.fields.show)
          .withQueryParam("limit", req.limit.show)
          .withQueryParam("offset", req.offset.show)
      }
}
