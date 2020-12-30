package spotification.album

import cats.syntax.show._
import eu.timepit.refined.cats._
import org.http4s.Method.GET
import org.http4s.Uri
import spotification.album.json.implicits.GetAlbumSampleTrackResponseDecoder
import spotification.album.service.GetAlbumSampleTrackServiceEnv
import spotification.authorization.httpclient.authorizationBearerHeader
import spotification.common.httpclient.{H4sClient, HttpClientEnv, doRequest, eitherUriStringToH4s}
import spotification.common.json.implicits.ErrorResponseDecoder
import spotification.config.AlbumConfig
import spotification.config.service.AlbumConfigEnv
import spotification.track.TrackId
import zio.interop.catz.monadErrorInstance
import zio.{Has, Task, URLayer, ZLayer}

package object httpclient {
  import H4sClient.Dsl._

  val GetAlbumSampleTrackServiceLayer: URLayer[AlbumConfigEnv with HttpClientEnv, GetAlbumSampleTrackServiceEnv] =
    ContextLayer >>> ZLayer.fromService(ctx => getAlbumSampleTrack(ctx, _))

  private def getAlbumSampleTrack(ctx: Context, req: GetAlbumSampleTrackRequest): Task[TrackId] = {
    val h4sUri = eitherUriStringToH4s(albumsTracksUri(ctx.albumApiUri, req.albumId)).map {
      _.withQueryParam("limit", req.limit.show)
        .withQueryParam("offset", req.offset.show)
    }

    val get = GET(_: Uri, authorizationBearerHeader(req.accessToken))

    Task
      .fromEither(h4sUri)
      .flatMap(doRequest[GetAlbumSampleTrackResponse](ctx.httpClient, _)(get))
      .map(_.items.headOption)
      .flatMap {
        case Some(track) => Task.succeed(track.id)
        case None        => Task.fail(new Exception(show"Album ${req.albumId} without tracks???"))
      }
  }

  private final case class Context(albumApiUri: AlbumApiUri, httpClient: H4sClient)
  private lazy val ContextLayer: URLayer[AlbumConfigEnv with HttpClientEnv, Has[Context]] =
    ZLayer.fromServices[AlbumConfig, H4sClient, Context] { (config, httpClient) =>
      Context(config.albumApiUri, httpClient)
    }
}
