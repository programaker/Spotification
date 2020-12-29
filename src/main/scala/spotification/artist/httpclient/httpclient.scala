package spotification.artist

import cats.syntax.either._
import cats.syntax.show._
import eu.timepit.refined.auto._
import eu.timepit.refined.cats._
import org.http4s.Method.GET
import org.http4s.Uri
import spotification.artist.GetArtistsAlbumsRequest.RequestType
import spotification.artist.json.implicits.GetArtistsAlbumsResponseDecoder
import spotification.artist.service.GetArtistsAlbumsServiceEnv
import spotification.authorization.httpclient.authorizationBearerHeader
import spotification.common.httpclient.{H4sClient, HttpClientEnv, doRequest, eitherUriStringToH4s}
import spotification.common.json.implicits.ErrorResponseDecoder
import spotification.config.ArtistConfig
import spotification.config.service.ArtistConfigEnv
import zio.interop.catz.monadErrorInstance
import zio.{Has, Task, URLayer, ZLayer}

package object httpclient {
  import H4sClient.Dsl._

  val GetArtistsAlbumsServiceLayer: URLayer[ArtistConfigEnv with HttpClientEnv, GetArtistsAlbumsServiceEnv] =
    ContextLayer >>> ZLayer.fromService(ctx => getArtistsAlbums(ctx, _))

  private def getArtistsAlbums(ctx: Context, req: GetArtistsAlbumsRequest[_]): Task[GetArtistsAlbumsResponse] = {
    val h4sUri = req.requestType match {
      case RequestType.First(artistId, include_groups, limit, offset) =>
        for {
          uri <- eitherUriStringToH4s(artistsAlbumsUri(ctx.artistApiUri, artistId))
          ig  <- joinIncludeAlbumGroups(include_groups).leftMap(new Exception(_))
        } yield uri
          .withQueryParam("include_groups", ig.show)
          .withQueryParam("limit", limit.show)
          .withQueryParam("offset", offset.show)

      case RequestType.Next(uri) =>
        Uri.fromString(uri)
    }

    val get = GET(_: Uri, authorizationBearerHeader(req.accessToken))
    Task.fromEither(h4sUri).flatMap(doRequest[GetArtistsAlbumsResponse](ctx.httpClient, _)(get))
  }

  private final case class Context(artistApiUri: ArtistApiUri, httpClient: H4sClient)
  private lazy val ContextLayer: URLayer[ArtistConfigEnv with HttpClientEnv, Has[Context]] =
    ZLayer.fromServices[ArtistConfig, H4sClient, Context] { (config, httpClient) =>
      Context(config.artistApiUri, httpClient)
    }
}
