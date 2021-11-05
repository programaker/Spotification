package spotification.album

import cats.syntax.show._
import eu.timepit.refined.cats._
import org.http4s.Method.GET
import org.http4s.{EntityDecoder, Uri}
import spotification.album.json.implicits.GetAlbumSampleTrackResponseDecoder
import spotification.album.service.{GetAlbumSampleTrackService, GetAlbumSampleTrackServiceR}
import spotification.authorization.httpclient.authorizationBearerHeader
import spotification.common.httpclient.{H4sClient, HttpClientR, doRequest, uriStringToUri}
import spotification.common.json.implicits.{ErrorResponseDecoder, entityDecoderF}
import spotification.config.AlbumConfig
import spotification.config.service.AlbumConfigR
import zio.interop.catz.concurrentInstance
import zio.{Task, URLayer, ZLayer}

package object httpclient {
  import H4sClient.Dsl._

  val GetAlbumSampleTrackServiceLayer: URLayer[AlbumConfigR with HttpClientR, GetAlbumSampleTrackServiceR] =
    ZLayer.fromServices[AlbumConfig, H4sClient, GetAlbumSampleTrackService] { (config, http) => req =>
      implicit val ed: EntityDecoder[Task, String] = entityDecoderF

      val h4sUri = albumsTracksUri(config.albumApiUri, req.albumId).flatMap(uriStringToUri).map {
        _.withQueryParam("limit", req.limit.show)
          .withQueryParam("offset", req.offset.show)
      }

      val get = GET(_: Uri, authorizationBearerHeader(req.accessToken))

      doRequest[GetAlbumSampleTrackResponse](http, h4sUri)(get)
        .map(_.items.headOption)
        .flatMap {
          case Some(track) => Task.succeed(track.id)
          case None        => Task.fail(new Exception(show"Album ${req.albumId} with a single track???"))
        }
    }
}
