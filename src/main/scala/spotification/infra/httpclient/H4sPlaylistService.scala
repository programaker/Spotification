package spotification.infra.httpclient

import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.Method._
import org.http4s.Uri
import spotification.domain.spotify.playlist._
import spotification.infra.httpclient.AuthorizationHttpClient.authorizationBearerHeader
import spotification.infra.httpclient.HttpClient.{H4sClientDsl, doRequest}
import spotification.infra.spotify.playlist.PlaylistModule
import zio.Task
import eu.timepit.refined.cats._
import eu.timepit.refined.auto._
import spotification.domain.spotify.playlist.GetPlaylistsItemsRequest.{FirstRequest, NextRequest}
import spotification.domain.spotify.playlist.Playlist.playlistTracksUri
import zio.interop.catz.monadErrorInstance
import spotification.infra.Json.Implicits.FEntityEncoder
import io.circe.refined._

final class H4sPlaylistService(playlistApiUri: PlaylistApiUri, httpClient: H4sClient) extends PlaylistModule.Service {
  import H4sClientDsl._

  override def getPlaylistsItems(req: GetPlaylistsItemsRequest): Task[GetPlaylistsItemsResponse.Success] = {
    val accessToken = GetPlaylistsItemsRequest.accessToken(req)
    val get = GET(_: Uri, authorizationBearerHeader(accessToken))

    val uri = req match {
      case fr: FirstRequest        => getItemsUri(fr)
      case NextRequest(nextUri, _) => Uri.fromString(nextUri)
    }

    doRequest[GetPlaylistsItemsResponse](httpClient, uri)(get).flatMap {
      case s: GetPlaylistsItemsResponse.Success =>
        Task.succeed(s)
      case GetPlaylistsItemsResponse.Error(status, message) =>
        Task.fail(new Exception(show"Error in GetPlaylistsItems: status=$status, message='$message'"))
    }
  }

  override def addItemsToPlaylist(req: AddItemsToPlaylistRequest): Task[AddItemsToPlaylistResponse.Success] = {
    val post = POST(req.body.asJson, _: Uri, authorizationBearerHeader(req.accessToken))

    doRequest[AddItemsToPlaylistResponse](httpClient, tracksUri(req.playlistId))(post).flatMap {
      case s: AddItemsToPlaylistResponse.Success =>
        Task.succeed(s)
      case AddItemsToPlaylistResponse.Error(status, message) =>
        Task.fail(new Exception(show"Error in AddItemsToPlaylist: status=$status, message='$message'"))
    }
  }

  override def removeItemsFromPlaylist(
    req: RemoveItemsFromPlaylistRequest
  ): Task[RemoveItemsFromPlaylistResponse.Success] = {
    val delete = DELETE(req.body.asJson, _: Uri, authorizationBearerHeader(req.accessToken))

    doRequest[RemoveItemsFromPlaylistResponse](httpClient, tracksUri(req.playlistId))(delete).flatMap {
      case s: RemoveItemsFromPlaylistResponse.Success =>
        Task.succeed(s)
      case RemoveItemsFromPlaylistResponse.Error(status, message) =>
        Task.fail(new Exception(show"Error in RemoveItemsFromPlaylist: status=$status, message='$message'"))
    }
  }

  private def getItemsUri(req: FirstRequest): Either[Throwable, Uri] =
    tracksUri(req.playlistId).map {
      _.withQueryParam("fields", GetPlaylistsItemsResponse.Fields.show)
        .withQueryParam("limit", req.limit.show)
        .withQueryParam("offset", req.offset.show)
    }

  private def tracksUri(playlistId: PlaylistId): Either[Throwable, Uri] =
    playlistTracksUri(playlistApiUri, playlistId)
      .leftMap(new Exception(_))
      .flatMap(Uri.fromString(_))
}
