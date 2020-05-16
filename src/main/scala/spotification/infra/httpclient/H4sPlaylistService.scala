package spotification.infra.httpclient

import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.Method._
import org.http4s.{ParseFailure, Uri}
import spotification.domain.spotify.playlist._
import spotification.infra.httpclient.AuthorizationHttpClient.authorizationBearerHeader
import spotification.infra.httpclient.HttpClient.{H4sClientDsl, doRequest}
import spotification.infra.spotify.playlist.PlaylistModule
import zio.Task
import eu.timepit.refined.cats._
import eu.timepit.refined.auto._
import spotification.domain.spotify.playlist.GetPlaylistsItemsRequest.{FirstRequest, NextRequest}

// ==========
// Despite IntelliJ telling that
// `import zio.interop.catz._`
// `import io.circe.refined._`
// `import spotification.infra.Json.Implicits._`
// are not being used, they are required to compile
// ==========
import zio.interop.catz._
import io.circe.refined._
import spotification.infra.Json.Implicits._

final class H4sPlaylistService(playlistApiUri: PlaylistApiUri, httpClient: H4sClient) extends PlaylistModule.Service {
  import H4sClientDsl._

  override def getPlaylistsItems(req: GetPlaylistsItemsRequest): Task[GetPlaylistsItemsResponse] = {
    val (accessToken, uri) = req match {
      case first: FirstRequest               => (first.accessToken, getItemsUri(first))
      case NextRequest(accessToken, nextUri) => (accessToken, Uri.fromString(nextUri))
    }

    val get = GET(_: Uri, authorizationBearerHeader(accessToken))
    doRequest[GetPlaylistsItemsResponse](httpClient, uri)(get)
  }

  override def addItemsToPlaylist(req: AddItemsToPlaylistRequest): Task[AddItemsToPlaylistResponse] = {
    val post = POST(req.body.asJson, _: Uri, authorizationBearerHeader(req.accessToken))
    doRequest[AddItemsToPlaylistResponse](httpClient, tracksUri(req.playlistId))(post)
  }

  private def getItemsUri(req: FirstRequest): Either[ParseFailure, Uri] =
    tracksUri(req.playlistId).map {
      _.withQueryParam("fields", req.fields.show)
        .withQueryParam("limit", req.limit.show)
        .withQueryParam("offset", req.offset.show)
    }

  private def tracksUri(playlistId: PlaylistId): Either[ParseFailure, Uri] =
    Uri.fromString(show"$playlistApiUri/$playlistId/tracks")
}
