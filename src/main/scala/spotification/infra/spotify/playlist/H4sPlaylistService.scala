package spotification.infra.spotify.playlist

import cats.implicits._
import eu.timepit.refined.auto._
import eu.timepit.refined.cats._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.Method.{DELETE, GET, POST}
import org.http4s.Uri
import spotification.domain.spotify.playlist.GetPlaylistsItemsRequest.{FirstRequest, NextRequest}
import spotification.domain.spotify.playlist._
import spotification.infra.httpclient._
import spotification.infra.json.implicits._
import zio._
import zio.interop.catz.monadErrorInstance
import io.circe.refined._

final class H4sPlaylistService(playlistApiUri: PlaylistApiUri, httpClient: H4sClient) extends PlaylistService {
  import H4sClient.dsl._

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
