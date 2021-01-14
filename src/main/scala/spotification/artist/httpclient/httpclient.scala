package spotification.artist

import cats.syntax.either._
import cats.syntax.show._
import eu.timepit.refined.auto._
import eu.timepit.refined.cats._
import org.http4s.Method.GET
import org.http4s.Uri
import spotification.artist.GetArtistsAlbumsRequest.RequestType
import spotification.artist.json.implicits.GetArtistsAlbumsResponseDecoder
import spotification.artist.service.{GetArtistsAlbumsService, GetArtistsAlbumsServiceR}
import spotification.authorization.httpclient.authorizationBearerHeader
import spotification.common.httpclient.{H4sClient, HttpClientR, doRequest, eitherUriStringToH4s}
import spotification.common.json.implicits.ErrorResponseDecoder
import spotification.config.ArtistConfig
import spotification.config.service.ArtistConfigR
import zio.interop.catz.monadErrorInstance
import zio.{Task, URLayer, ZLayer}

package object httpclient {
  import H4sClient.Dsl._

  val GetArtistsAlbumsServiceLayer: URLayer[ArtistConfigR with HttpClientR, GetArtistsAlbumsServiceR] =
    ZLayer.fromServices[ArtistConfig, H4sClient, GetArtistsAlbumsService] { (config, http) => req =>
      val h4sUri = req.requestType match {
        case RequestType.First(artistId, include_groups, limit, offset) =>
          for {
            uri <- eitherUriStringToH4s(artistsAlbumsUri(config.artistApiUri, artistId))
            ig  <- joinIncludeAlbumGroups(include_groups).leftMap(new Exception(_))
          } yield uri
            .withQueryParam("include_groups", ig.show)
            .withQueryParam("limit", limit.show)
            .withQueryParam("offset", offset.show)

        case RequestType.Next(uri) =>
          Uri.fromString(uri)
      }

      val get = GET(_: Uri, authorizationBearerHeader(req.accessToken))
      Task.fromEither(h4sUri).flatMap(doRequest[GetArtistsAlbumsResponse](http, _)(get))
    }
}
