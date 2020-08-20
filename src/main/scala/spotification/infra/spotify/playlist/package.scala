package spotification.infra.spotify

import cats.implicits._
import eu.timepit.refined.auto._
import eu.timepit.refined.cats._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.Method.{DELETE, GET, POST}
import org.http4s.Uri
import spotification.domain.config.PlaylistConfig
import spotification.domain.spotify.playlist.GetPlaylistsItemsRequest.{FirstRequest, NextRequest}
import spotification.domain.spotify.playlist._
import spotification.infra.json.implicits._
import spotification.infra.config.PlaylistConfigModule
import spotification.infra.httpclient._
import zio._
import zio.interop.catz.monadErrorInstance
import io.circe.refined._

package object playlist {
  type PlaylistModule = Has[PlaylistModule.Service]
  object PlaylistModule {
    def getPlaylistItems(req: GetPlaylistsItemsRequest): RIO[PlaylistModule, GetPlaylistsItemsResponse] =
      ZIO.accessM(_.get.getPlaylistsItems(req))

    def addItemsToPlaylist(req: AddItemsToPlaylistRequest): RIO[PlaylistModule, PlaylistSnapshotResponse] =
      ZIO.accessM(_.get.addItemsToPlaylist(req))

    def removeItemsFromPlaylist(
      req: RemoveItemsFromPlaylistRequest
    ): RIO[PlaylistModule, PlaylistSnapshotResponse] =
      ZIO.accessM(_.get.removeItemsFromPlaylist(req))

    val layer: TaskLayer[PlaylistModule] = {
      val l1 = ZLayer.fromServices[PlaylistConfig, H4sClient, PlaylistModule.Service] { (config, httpClient) =>
        new H4sPlaylistService(config.playlistApiUri, httpClient)
      }

      (PlaylistConfigModule.layer ++ HttpClientModule.layer) >>> l1
    }

    trait Service {
      def getPlaylistsItems(req: GetPlaylistsItemsRequest): Task[GetPlaylistsItemsResponse]
      def addItemsToPlaylist(req: AddItemsToPlaylistRequest): Task[PlaylistSnapshotResponse]
      def removeItemsFromPlaylist(req: RemoveItemsFromPlaylistRequest): Task[PlaylistSnapshotResponse]
    }
  }

  final private class H4sPlaylistService(playlistApiUri: PlaylistApiUri, httpClient: H4sClient)
      extends PlaylistModule.Service {

    import H4sClientDsl._

    override def getPlaylistsItems(req: GetPlaylistsItemsRequest): Task[GetPlaylistsItemsResponse] = {
      val get = GET(_: Uri, authorizationBearerHeader(GetPlaylistsItemsRequest.accessToken(req)))

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
        _.withQueryParam("fields", GetPlaylistsItemsResponse.Fields.show)
          .withQueryParam("limit", req.limit.show)
          .withQueryParam("offset", req.offset.show)
      }

    private def tracksUri(playlistId: PlaylistId): Either[Throwable, Uri] =
      playlistTracksUri(playlistApiUri, playlistId)
        .leftMap(new Exception(_))
        .flatMap(Uri.fromString(_))
  }
}
