package spotification.me

import cats.syntax.show._
import eu.timepit.refined.auto._
import eu.timepit.refined.cats._
import org.http4s.Method.GET
import org.http4s.Uri
import spotification.authorization.httpclient.authorizationBearerHeader
import spotification.common.httpclient.{H4sClient, HttpClientEnv, doRequest, eitherUriStringToH4s}
import spotification.common.json.implicits.ErrorResponseDecoder
import spotification.config.MeConfig
import spotification.config.service.MeConfigEnv
import spotification.me.GetMyFollowedArtistsRequest.RequestType.{First, Next}
import spotification.me.json.implicits.{GetMyFollowedArtistsResponseDecoder, GetMyProfileResponseDecoder}
import spotification.me.service.{
  GetMyFollowedArtistsService,
  GetMyFollowedArtistsServiceEnv,
  GetMyProfileService,
  GetMyProfileServiceEnv
}
import zio._
import zio.interop.catz.monadErrorInstance

package object httpclient {
  import H4sClient.Dsl._

  val GetMyProfileServiceLayer: URLayer[MeConfigEnv with HttpClientEnv, GetMyProfileServiceEnv] =
    ZLayer.fromServices[MeConfig, H4sClient, GetMyProfileService] { (config, http) => req =>
      Task
        .fromEither(Uri.fromString(config.meApiUri.show))
        .flatMap(doRequest[GetMyProfileResponse](http, _)(GET(_, authorizationBearerHeader(req.accessToken))))
    }

  val GetMyFollowedArtistsServiceLayer: URLayer[MeConfigEnv with HttpClientEnv, GetMyFollowedArtistsServiceEnv] =
    ZLayer.fromServices[MeConfig, H4sClient, GetMyFollowedArtistsService] { (config, http) => req =>
      val h4sUri = req.requestType match {
        case First(followType, limit) =>
          val addQueryParams: Uri => Uri =
            _.withQueryParam("type", followType.show)
              .withOptionQueryParam("limit", limit.map(_.show))

          eitherUriStringToH4s(makeMyFollowedArtistsUri(config.meApiUri)).map(addQueryParams)

        case Next(nextUri) =>
          Uri.fromString(nextUri)
      }

      val get = GET(_: Uri, authorizationBearerHeader(req.accessToken))
      Task.fromEither(h4sUri).flatMap(doRequest[GetMyFollowedArtistsResponse](http, _)(get))
    }
}
