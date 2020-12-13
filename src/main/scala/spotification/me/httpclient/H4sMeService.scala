package spotification.me.httpclient

import cats.syntax.show._
import eu.timepit.refined.cats.refTypeShow
import eu.timepit.refined.auto._
import io.circe.generic.auto._
import io.circe.refined._
import org.http4s.Method.GET
import org.http4s.Uri
import spotification.authorization.httpclient.authorizationBearerHeader
import spotification.common.httpclient.{H4sClient, doRequest, eitherUriStringToH4s}
import spotification.me.GetMyFollowedArtistsRequest.{FirstRequest, NextRequest}
import spotification.me.service.MeService
import spotification.me.{
  GetMyFollowedArtistsRequest,
  GetMyFollowedArtistsResponse,
  GetMyProfileRequest,
  GetMyProfileResponse,
  MeApiUri,
  makeMyFollowedArtistsUri
}
import spotification.json.implicits._
import zio.Task
import zio.interop.catz.monadErrorInstance

final class H4sMeService(meApiUri: MeApiUri, httpClient: H4sClient) extends MeService {
  import H4sClient.Dsl._

  override def getMyProfile(req: GetMyProfileRequest): Task[GetMyProfileResponse] =
    Task
      .fromEither(Uri.fromString(meApiUri.show))
      .flatMap(doRequest[GetMyProfileResponse](httpClient, _)(GET(_, authorizationBearerHeader(req.accessToken))))

  override def getMyFollowedArtists(req: GetMyFollowedArtistsRequest): Task[GetMyFollowedArtistsResponse] = {
    val (token, h4sUri) = req match {
      case FirstRequest(accessToken, followType, limit) =>
        val addQueryParams: Uri => Uri =
          _.withQueryParam("type", followType.show)
            .withOptionQueryParam("limit", limit.map(_.show))

        (accessToken, eitherUriStringToH4s(makeMyFollowedArtistsUri(meApiUri)).map(addQueryParams))

      case NextRequest(accessToken, nextUri) =>
        (accessToken, Uri.fromString(nextUri))
    }

    val get = GET(_: Uri, authorizationBearerHeader(token))
    Task.fromEither(h4sUri).flatMap(doRequest[GetMyFollowedArtistsResponse](httpClient, _)(get))
  }
}
