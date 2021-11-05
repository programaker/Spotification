package spotification.playlist

import cats.syntax.show._
import eu.timepit.refined.auto._
import eu.timepit.refined.cats._
import io.circe.syntax.EncoderOps
import org.http4s.Method.{DELETE, GET, POST}
import org.http4s.Uri
import spotification.authorization.httpclient.authorizationBearerHeader
import spotification.common.httpclient.{H4sClient, HttpClientR, doRequest, uriStringToUri}
import spotification.common.json.implicits.{ErrorResponseDecoder, entityEncoderF}
import spotification.config.service.{PlaylistConfigR, UserConfigR}
import spotification.config.{PlaylistConfig, UserConfig}
import spotification.playlist.GetPlaylistsItemsRequest.RequestType.{First, Next}
import spotification.playlist.json.implicits.{
  AddItemsToPlaylistRequestBodyEncoder,
  CreatePlaylistRequestBodyEncoder,
  CreatePlaylistResponseDecoder,
  GetPlaylistsItemsResponseDecoder,
  PlaylistSnapshotResponseDecoder,
  RemoveItemsFromPlaylistRequestEncoder
}
import spotification.playlist.service._
import zio._
import zio.interop.catz.concurrentInstance

package object httpclient {
  import H4sClient.Dsl._

  val GetPlaylistsItemsServiceLayer: URLayer[PlaylistConfigR with HttpClientR, GetPlaylistItemsServiceR] =
    ZLayer.fromServices[PlaylistConfig, H4sClient, GetPlaylistItemsService] { (config, http) => req =>
      val h4sUri = req.requestType match {
        case First(playlistId, limit, offset) =>
          tracksUri(config.playlistApiUri, playlistId).map {
            _.withQueryParam("fields", GetPlaylistsItemsResponse.Fields.show)
              .withQueryParam("limit", limit.show)
              .withQueryParam("offset", offset.show)
          }

        case Next(nextUri) =>
          Uri.fromString(nextUri)
      }

      val get = GET(_: Uri, authorizationBearerHeader(req.accessToken))
      doRequest[GetPlaylistsItemsResponse](http, h4sUri)(get)
    }

  val AddItemsToPlaylistServiceLayer: URLayer[PlaylistConfigR with HttpClientR, AddItemsToPlaylistServiceR] =
    ZLayer.fromServices[PlaylistConfig, H4sClient, AddItemsToPlaylistService] { (config, http) => req =>
      val post = POST(req.body.asJson, _: Uri, authorizationBearerHeader(req.accessToken))
      val uri = tracksUri(config.playlistApiUri, req.playlistId)
      doRequest[PlaylistSnapshotResponse](http, uri)(post)
    }

  val RemoveItemsFromPlaylistServiceLayer: URLayer[PlaylistConfigR with HttpClientR, RemoveItemsFromPlaylistServiceR] =
    ZLayer.fromServices[PlaylistConfig, H4sClient, RemoveItemsFromPlaylistService] { (config, http) => req =>
      val delete = DELETE(req.body.asJson, _: Uri, authorizationBearerHeader(req.accessToken))
      val uri = tracksUri(config.playlistApiUri, req.playlistId)
      doRequest[PlaylistSnapshotResponse](http, uri)(delete)
    }

  val CreatePlaylistServiceLayer: URLayer[UserConfigR with HttpClientR, CreatePlaylistServiceR] =
    ZLayer.fromServices[UserConfig, H4sClient, CreatePlaylistService] { (config, http) => req =>
      val post = POST(req.body.asJson, _: Uri, authorizationBearerHeader(req.accessToken))
      val uri = userPlaylistsUri(config.userApiUri, req.userId).flatMap(uriStringToUri)
      doRequest[CreatePlaylistResponse](http, uri)(post)
    }

  private def tracksUri(playlistApiUri: PlaylistApiUri, playlistId: PlaylistId): Either[Throwable, Uri] =
    playlistTracksUri(playlistApiUri, playlistId).flatMap(uriStringToUri)
}
