package spotification.playlist.httpclient

import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import eu.timepit.refined.auto._
import eu.timepit.refined.cats._
import org.http4s.Method.{DELETE, GET, POST}
import org.http4s.Uri
import spotification.playlist.GetPlaylistsItemsRequest.RequestType.{First, Next}
import spotification.playlist._
import spotification.playlist.service.PlaylistService
import zio.Task
import zio.interop.catz.monadErrorInstance
import spotification.json.implicits._
import io.circe.refined._
import spotification.authorization.httpclient.authorizationBearerHeader
import spotification.common.httpclient.{H4sClient, doRequest, eitherUriStringToH4s}

final class H4sPlaylistService(playlistApiUri: PlaylistApiUri, httpClient: H4sClient) extends PlaylistService {
  import H4sClient.Dsl._

  override def getPlaylistsItems(req: GetPlaylistsItemsRequest[_]): Task[GetPlaylistsItemsResponse] = {
    val h4sUri = req.requestType match {
      case First(playlistId, limit, offset) =>
        tracksUri(playlistId).map {
          _.withQueryParam("fields", GetPlaylistsItemsResponse.Fields.show)
            .withQueryParam("limit", limit.show)
            .withQueryParam("offset", offset.show)
        }

      case Next(nextUri) =>
        Uri.fromString(nextUri)
    }

    val get = GET(_: Uri, authorizationBearerHeader(req.accessToken))
    Task.fromEither(h4sUri).flatMap(doRequest[GetPlaylistsItemsResponse](httpClient, _)(get))
  }

  override def addItemsToPlaylist(req: AddItemsToPlaylistRequest): Task[PlaylistSnapshotResponse] = {
    val post = POST(req.body.asJson, _: Uri, authorizationBearerHeader(req.accessToken))
    Task.fromEither(tracksUri(req.playlistId)).flatMap(doRequest[PlaylistSnapshotResponse](httpClient, _)(post))
  }

  override def removeItemsFromPlaylist(req: RemoveItemsFromPlaylistRequest): Task[PlaylistSnapshotResponse] = {
    val delete = DELETE(req.body.asJson, _: Uri, authorizationBearerHeader(req.accessToken))
    Task.fromEither(tracksUri(req.playlistId)).flatMap(doRequest[PlaylistSnapshotResponse](httpClient, _)(delete))
  }

  private def tracksUri(playlistId: PlaylistId): Either[Throwable, Uri] =
    eitherUriStringToH4s(playlistTracksUri(playlistApiUri, playlistId))
}
