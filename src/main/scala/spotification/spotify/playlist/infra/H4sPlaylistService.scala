package spotification.spotify.playlist.infra

import cats.implicits._
import eu.timepit.refined.cats._
import eu.timepit.refined.auto._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.Method.{DELETE, GET, POST}
import org.http4s.Uri
import spotification.common.infra.httpclient.{H4sClient, authorizationBearerHeader, doRequest}
import spotification.spotify.playlist.GetPlaylistsItemsRequest.{FirstRequest, NextRequest}
import spotification.spotify.playlist._
import spotification.spotify.playlist.application.PlaylistService
import zio._
import zio.interop.catz._

final class H4sPlaylistService(playlistApiUri: PlaylistApiUri, httpClient: H4sClient) extends PlaylistService {

  override def getPlaylistsItems(req: GetPlaylistsItemsRequest): Task[GetPlaylistsItemsResponse] = {
    val get = GET(_: Uri, authorizationBearerHeader(accessTokenFromRequest(req)))

    Task
      .fromEither(req match {
        case fr: FirstRequest        => getItemsUri(fr)
        case NextRequest(nextUri, _) => Uri.fromString(nextUri)
      })
      .flatMap(doRequest[GetPlaylistsItemsResponse](httpClient, _)(get))
  }

  override def addItemsToPlaylist(req: AddItemsToPlaylistRequest): Task[PlaylistSnapshotResponse] = {
    val post = POST(req.body.asJson, _: Uri, authorizationBearerHeader(req.accessToken))

    Task
      .fromEither(tracksUri(req.playlistId))
      .flatMap(doRequest[PlaylistSnapshotResponse](httpClient, _)(post))
  }

  override def removeItemsFromPlaylist(req: RemoveItemsFromPlaylistRequest): Task[PlaylistSnapshotResponse] = {
    val delete = DELETE(req.body.asJson, _: Uri, authorizationBearerHeader(req.accessToken))

    Task
      .fromEither(tracksUri(req.playlistId))
      .flatMap(doRequest[PlaylistSnapshotResponse](httpClient, _)(delete))
  }

  private def getItemsUri(req: FirstRequest): Either[Throwable, Uri] =
    tracksUri(req.playlistId).map {
      _.withQueryParam("fields", GetPlaylistsItemsResponse.fields.show)
        .withQueryParam("limit", req.limit.show)
        .withQueryParam("offset", req.offset.show)
    }

  private def tracksUri(playlistId: PlaylistId): Either[Throwable, Uri] =
    playlistTracksUri(playlistApiUri, playlistId)
      .leftMap(new Exception(_))
      .flatMap(Uri.fromString(_))
}
