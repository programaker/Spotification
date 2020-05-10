package spotification.infra.httpclient

import cats.implicits._
import io.circe.jawn
import io.circe.generic.auto._
import org.http4s.Method._
import org.http4s.{ParseFailure, Uri}
import spotification.domain.spotify.playlist.{PlaylistItemsRequest, PlaylistItemsResponse}
import spotification.infra.BaseEnv
import spotification.infra.httpclient.AuthorizationHttpClient.authorizationBearerHeader
import spotification.infra.httpclient.HttpClient.{H4sClientDsl, _}
import spotification.infra.spotify.playlist.PlaylistModule
import zio.{RIO, Task}
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

final class H4sPlaylistService(httpClient: H4sClient) extends PlaylistModule.Service {
  import H4sClientDsl._
  private val PlaylistsApiUri: String = show"$ApiUri/playlists"

  override def getPlaylistItems(req: PlaylistItemsRequest): RIO[BaseEnv, PlaylistItemsResponse] = {
    val (accessToken, uri) = req match {
      case first: FirstRequest               => (first.accessToken, makeUri(first))
      case NextRequest(accessToken, nextUri) => (accessToken, Uri.fromString(nextUri))
    }

    RIO
      .fromEither(uri.leftMap(pf => new Exception(pf.message)))
      .map(GET(_, authorizationBearerHeader(accessToken)))
      .flatMap(httpClient.expect[String])
      .map(jawn.decode[PlaylistItemsResponse])
      .flatMap(Task.fromEither(_))
  }

  private def makeUri(req: FirstRequest): Either[ParseFailure, Uri] =
    Uri
      .fromString(show"$PlaylistsApiUri/${req.playlistId}/tracks")
      .map {
        _.withQueryParam("fields", req.fields.show)
          .withQueryParam("limit", req.limit.show)
          .withQueryParam("offset", req.offset.show)
      }
}
