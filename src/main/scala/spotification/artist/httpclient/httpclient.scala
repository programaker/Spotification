package spotification.artist

import cats.syntax.show._
import eu.timepit.refined.auto._
import eu.timepit.refined.cats._
import org.http4s.Method.GET
import org.http4s.Uri
import spotification.artist.GetArtistsAlbumsRequest.RequestType
import spotification.artist.GetMyFollowedArtistsRequest.RequestType.{First, Next}
import spotification.artist.json.implicits.{GetArtistsAlbumsResponseDecoder, GetMyFollowedArtistsResponseDecoder}
import spotification.artist.service.{
  GetArtistsAlbumsService,
  GetArtistsAlbumsServiceR,
  GetMyFollowedArtistsService,
  GetMyFollowedArtistsServiceR
}
import spotification.authorization.httpclient.authorizationBearerHeader
import spotification.common.httpclient.{H4sClient, HttpClientR, doRequest, uriStringToUri}
import spotification.common.json.implicits.ErrorResponseDecoder
import spotification.config.service.{ArtistConfigR, MeConfigR}
import spotification.config.{ArtistConfig, MeConfig}
import zio.interop.catz.monadErrorInstance
import zio.{URLayer, ZLayer}

package object httpclient {
  import H4sClient.Dsl._

  val GetMyFollowedArtistsServiceLayer: URLayer[MeConfigR with HttpClientR, GetMyFollowedArtistsServiceR] =
    ZLayer.fromServices[MeConfig, H4sClient, GetMyFollowedArtistsService] { (config, http) => req =>
      val h4sUri = req.requestType match {
        case First(followType, limit) =>
          makeMyFollowedArtistsUri(config.meApiUri).flatMap(uriStringToUri).map {
            _.withQueryParam("type", followType.show)
              .withOptionQueryParam("limit", limit.map(_.show))
          }

        case Next(nextUri) =>
          Uri.fromString(nextUri)
      }

      val get = GET(_: Uri, authorizationBearerHeader(req.accessToken))
      doRequest[GetMyFollowedArtistsResponse](http, h4sUri)(get)
    }

  val GetArtistsAlbumsServiceLayer: URLayer[ArtistConfigR with HttpClientR, GetArtistsAlbumsServiceR] =
    ZLayer.fromServices[ArtistConfig, H4sClient, GetArtistsAlbumsService] { (config, http) => req =>
      val h4sUri = req.requestType match {
        case RequestType.First(artistId, include_groups, limit, offset) =>
          for {
            uriString <- artistsAlbumsUri(config.artistApiUri, artistId)
            uri       <- uriStringToUri(uriString)
            ig        <- joinIncludeAlbumGroups(include_groups)
          } yield uri
            .withQueryParam("include_groups", ig.show)
            .withQueryParam("limit", limit.show)
            .withQueryParam("offset", offset.show)

        case RequestType.Next(uri) =>
          Uri.fromString(uri)
      }

      val get = GET(_: Uri, authorizationBearerHeader(req.accessToken))
      doRequest[GetArtistsAlbumsResponse](http, h4sUri)(get)
    }
}
