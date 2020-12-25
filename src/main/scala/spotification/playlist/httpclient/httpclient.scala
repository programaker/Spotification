package spotification.playlist

import cats.syntax.show._
import eu.timepit.refined.auto._
import eu.timepit.refined.cats._
import io.circe.syntax.EncoderOps
import org.http4s.Method.{DELETE, GET, POST}
import org.http4s.Uri
import spotification.authorization.httpclient.authorizationBearerHeader
import spotification.common.httpclient.{H4sClient, HttpClientLayer, doRequest, eitherUriStringToH4s}
import spotification.common.json.implicits.{ErrorResponseDecoder, entityEncoderF}
import spotification.config.PlaylistConfig
import spotification.config.source.PlaylistConfigLayer
import spotification.playlist.GetPlaylistsItemsRequest.RequestType.{First, Next}
import spotification.playlist.json.implicits.{
  AddItemsToPlaylistRequestBodyEncoder,
  GetPlaylistsItemsResponseDecoder,
  PlaylistSnapshotResponseDecoder,
  RemoveItemsFromPlaylistRequestEncoder
}
import spotification.playlist.service._
import zio._
import zio.interop.catz.monadErrorInstance

package object httpclient {
  import H4sClient.Dsl._

  val GetPlaylistsItemsServiceLayer: TaskLayer[GetPlaylistItemsServiceEnv] =
    ContextLayer >>> ZLayer.fromService[Context, GetPlaylistItemsService](ctx => getPlaylistsItems(ctx, _))

  val AddItemsToPlaylistServiceLayer: TaskLayer[AddItemsToPlaylistServiceEnv] =
    ContextLayer >>> ZLayer.fromService[Context, AddItemsToPlaylistService](ctx => addItemsToPlaylist(ctx, _))

  val RemoveItemsFromPlaylistServiceLayer: TaskLayer[RemoveItemsFromPlaylistServiceEnv] =
    ContextLayer >>> ZLayer.fromService[Context, RemoveItemsFromPlaylistService](ctx => removeItemsFromPlaylist(ctx, _))

  private def getPlaylistsItems(ctx: Context, req: GetPlaylistsItemsRequest[_]): Task[GetPlaylistsItemsResponse] = {
    val h4sUri = req.requestType match {
      case First(playlistId, limit, offset) =>
        tracksUri(ctx.playlistApiUri, playlistId).map {
          _.withQueryParam("fields", GetPlaylistsItemsResponse.Fields.show)
            .withQueryParam("limit", limit.show)
            .withQueryParam("offset", offset.show)
        }

      case Next(nextUri) =>
        Uri.fromString(nextUri)
    }

    val get = GET(_: Uri, authorizationBearerHeader(req.accessToken))
    Task.fromEither(h4sUri).flatMap(doRequest[GetPlaylistsItemsResponse](ctx.httpClient, _)(get))
  }

  private def addItemsToPlaylist(ctx: Context, req: AddItemsToPlaylistRequest): Task[PlaylistSnapshotResponse] = {
    val post = POST(req.body.asJson, _: Uri, authorizationBearerHeader(req.accessToken))

    Task
      .fromEither(tracksUri(ctx.playlistApiUri, req.playlistId))
      .flatMap(doRequest[PlaylistSnapshotResponse](ctx.httpClient, _)(post))
  }

  private def removeItemsFromPlaylist(
    ctx: Context,
    req: RemoveItemsFromPlaylistRequest
  ): Task[PlaylistSnapshotResponse] = {
    val delete = DELETE(req.body.asJson, _: Uri, authorizationBearerHeader(req.accessToken))

    Task
      .fromEither(tracksUri(ctx.playlistApiUri, req.playlistId))
      .flatMap(doRequest[PlaylistSnapshotResponse](ctx.httpClient, _)(delete))
  }

  private def tracksUri(playlistApiUri: PlaylistApiUri, playlistId: PlaylistId): Either[Throwable, Uri] =
    eitherUriStringToH4s(playlistTracksUri(playlistApiUri, playlistId))

  private final case class Context(playlistApiUri: PlaylistApiUri, httpClient: H4sClient)
  private lazy val ContextLayer: TaskLayer[Has[Context]] =
    (PlaylistConfigLayer ++ HttpClientLayer) >>> ZLayer.fromServices[PlaylistConfig, H4sClient, Context] {
      (playlistConfig, httpClient) =>
        Context(playlistConfig.playlistApiUri, httpClient)
    }
}
