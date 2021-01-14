package spotification.album

import cats.syntax.show._
import eu.timepit.refined.cats._
import org.http4s.Method.GET
import org.http4s.Uri
import spotification.album.json.implicits.GetAlbumSampleTrackResponseDecoder
import spotification.album.service.{GetAlbumSampleTrackService, GetAlbumSampleTrackServiceEnv}
import spotification.authorization.httpclient.authorizationBearerHeader
import spotification.common.httpclient.{H4sClient, HttpClientEnv, doRequest, eitherUriStringToH4s}
import spotification.common.json.implicits.ErrorResponseDecoder
import spotification.config.AlbumConfig
import spotification.config.service.AlbumConfigEnv
import zio.interop.catz.monadErrorInstance
import zio.{Task, URLayer, ZLayer}

package object httpclient {
  import H4sClient.Dsl._

  val GetAlbumSampleTrackServiceLayer: URLayer[AlbumConfigEnv with HttpClientEnv, GetAlbumSampleTrackServiceEnv] =
    ZLayer.fromServices[AlbumConfig, H4sClient, GetAlbumSampleTrackService] { (config, http) => req =>
      val h4sUri = eitherUriStringToH4s(albumsTracksUri(config.albumApiUri, req.albumId)).map {
        _.withQueryParam("limit", req.limit.show)
          .withQueryParam("offset", req.offset.show)
      }

      val get = GET(_: Uri, authorizationBearerHeader(req.accessToken))

      Task
        .fromEither(h4sUri)
        .flatMap(doRequest[GetAlbumSampleTrackResponse](http, _)(get))
        .map(_.items.headOption)
        .flatMap {
          case Some(track) => Task.succeed(track.id)
          case None        => Task.fail(new Exception(show"Album ${req.albumId} with a single track???"))
        }
    }
}
