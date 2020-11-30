package spotification.artist.httpclient

import cats.implicits._
import eu.timepit.refined.auto._
import eu.timepit.refined.cats._
import io.circe.generic.auto._
import org.http4s.Method.GET
import org.http4s.Uri
import spotification.artist.GetMyFollowedArtistsRequest.{FirstRequest, NextRequest}
import spotification.artist.service.ArtistService
import spotification.artist.{GetMyFollowedArtistsRequest, GetMyFollowedArtistsResponse, makeMyFollowedArtistsUri}
import spotification.authorization.httpclient.authorizationBearerHeader
import spotification.common.httpclient.{H4sClient, doRequest, eitherUriStringToH4s}
import spotification.user.MeApiUri
import zio.Task
import zio.interop.catz.monadErrorInstance
import spotification.json.implicits._
import io.circe.refined._

final class H4sArtistService(meApiUri: MeApiUri, httpClient: H4sClient) extends ArtistService {
  import H4sClient.Dsl._

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
