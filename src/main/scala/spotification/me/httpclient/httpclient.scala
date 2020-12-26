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
    ContextLayer >>> ZLayer.fromService[Context, GetMyProfileService](ctx => getMyProfile(ctx, _))

  val GetMyFollowedArtistsServiceLayer: URLayer[MeConfigEnv with HttpClientEnv, GetMyFollowedArtistsServiceEnv] =
    ContextLayer >>> ZLayer.fromService[Context, GetMyFollowedArtistsService](ctx => getMyFollowedArtists(ctx, _))

  private def getMyProfile(ctx: Context, req: GetMyProfileRequest): Task[GetMyProfileResponse] =
    Task
      .fromEither(Uri.fromString(ctx.meApiUri.show))
      .flatMap(doRequest[GetMyProfileResponse](ctx.httpClient, _)(GET(_, authorizationBearerHeader(req.accessToken))))

  private def getMyFollowedArtists(
    ctx: Context,
    req: GetMyFollowedArtistsRequest[_]
  ): Task[GetMyFollowedArtistsResponse] = {
    val h4sUri = req.requestType match {
      case First(followType, limit) =>
        val addQueryParams: Uri => Uri =
          _.withQueryParam("type", followType.show)
            .withOptionQueryParam("limit", limit.map(_.show))

        eitherUriStringToH4s(makeMyFollowedArtistsUri(ctx.meApiUri)).map(addQueryParams)

      case Next(nextUri) =>
        Uri.fromString(nextUri)
    }

    val get = GET(_: Uri, authorizationBearerHeader(req.accessToken))
    Task.fromEither(h4sUri).flatMap(doRequest[GetMyFollowedArtistsResponse](ctx.httpClient, _)(get))
  }

  private final case class Context(meApiUri: MeApiUri, httpClient: H4sClient)
  private lazy val ContextLayer: URLayer[MeConfigEnv with HttpClientEnv, Has[Context]] =
    ZLayer.fromServices[MeConfig, H4sClient, Context] { (meConfig, httpClient) =>
      Context(meConfig.meApiUri, httpClient)
    }
}
