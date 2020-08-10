package spotification.infra.httpclient

import cats.implicits.showInterpolator
import cats.implicits.catsStdShowForInt
import cats.implicits.catsStdShowForString
import org.http4s.Method.GET
import org.http4s.Uri
import spotification.domain.spotify.track.{GetTrackRequest, GetTrackResponse, TrackApiUri}
import spotification.infra.Json.Implicits.GetTrackResponseDecoder
import spotification.infra.httpclient.AuthorizationHttpClient.authorizationBearerHeader
import spotification.infra.httpclient.HttpClient.{H4sClientDsl, doRequest}
import spotification.infra.spotify.track.TrackModule
import zio.Task
import zio.interop.catz.monadErrorInstance

final class H4sTrackService(trackApiUri: TrackApiUri, httpClient: H4sClient) extends TrackModule.Service {
  import H4sClientDsl._

  override def getTrack(req: GetTrackRequest): Task[GetTrackResponse.Success] = {
    val get = GET.apply(_: Uri, authorizationBearerHeader(req.accessToken))
    val uri = Uri.fromString(show"$trackApiUri/${req.trackId}")

    doRequest[GetTrackResponse](httpClient, uri)(get).flatMap {
      case s: GetTrackResponse.Success =>
        Task.succeed(s)
      case GetTrackResponse.Error(status, message) =>
        Task.fail(new Exception(show"Error in GetTrack: status=$status, message='$message'"))
    }
  }
}
