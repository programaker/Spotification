package spotification.playlist

import cats.syntax.show._
import eu.timepit.refined.auto._
import eu.timepit.refined.cats._
import io.circe.syntax.EncoderOps
import org.http4s.Method.{DELETE, GET, POST}
import org.http4s.Uri
import spotification.authorization.httpclient.authorizationBearerHeader
import spotification.common.httpclient.{H4sClient, HttpClientR, doRequest, eitherUriStringToH4s}
import spotification.common.json.implicits.{ErrorResponseDecoder, entityEncoderF}
import spotification.config.{PlaylistConfig, UserConfig}
import spotification.config.service.{PlaylistConfigR, UserConfigR}
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
import zio.interop.catz.monadErrorInstance

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
      Task.fromEither(h4sUri).flatMap(doRequest[GetPlaylistsItemsResponse](http, _)(get))
    }

  val AddItemsToPlaylistServiceLayer: URLayer[PlaylistConfigR with HttpClientR, AddItemsToPlaylistServiceR] =
    ZLayer.fromServices[PlaylistConfig, H4sClient, AddItemsToPlaylistService] { (config, http) => req =>
      val post = POST(req.body.asJson, _: Uri, authorizationBearerHeader(req.accessToken))

      Task
        .fromEither(tracksUri(config.playlistApiUri, req.playlistId))
        .flatMap(doRequest[PlaylistSnapshotResponse](http, _)(post))
    }

  val RemoveItemsFromPlaylistServiceLayer: URLayer[PlaylistConfigR with HttpClientR, RemoveItemsFromPlaylistServiceR] =
    ZLayer.fromServices[PlaylistConfig, H4sClient, RemoveItemsFromPlaylistService] { (config, http) => req =>
      val delete = DELETE(req.body.asJson, _: Uri, authorizationBearerHeader(req.accessToken))

      Task
        .fromEither(tracksUri(config.playlistApiUri, req.playlistId))
        .flatMap(doRequest[PlaylistSnapshotResponse](http, _)(delete))
    }

  val CreatePlaylistServiceLayer: URLayer[UserConfigR with HttpClientR, CreatePlaylistServiceR] =
    ZLayer.fromServices[UserConfig, H4sClient, CreatePlaylistService] { (config, http) => req =>
      val h4sUri = eitherUriStringToH4s(userPlaylistsUri(config.userApiUri, req.userId))
      val post = POST(req.body.asJson, _: Uri, authorizationBearerHeader(req.accessToken))
      Task.fromEither(h4sUri).flatMap(doRequest[CreatePlaylistResponse](http, _)(post))
    }

  private def tracksUri(playlistApiUri: PlaylistApiUri, playlistId: PlaylistId): Either[Throwable, Uri] =
    eitherUriStringToH4s(playlistTracksUri(playlistApiUri, playlistId))
}
