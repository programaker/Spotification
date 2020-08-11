package spotification.infra.httpclient

import cats.implicits._
import eu.timepit.refined.auto._
import org.http4s.Method.GET
import org.http4s.Uri
import spotification.domain.spotify.track.Track.trackUri
import spotification.domain.spotify.track._
import spotification.infra.Infra.eitherStringToTask
import spotification.infra.Json.Implicits.GetTrackResponseDecoder
import spotification.infra.httpclient.AuthorizationHttpClient.authorizationBearerHeader
import spotification.infra.httpclient.HttpClient.{H4sClientDsl, doRequest}
import spotification.infra.spotify.track.TrackModule
import zio.Task
import zio.interop.catz.monadErrorInstance

final class H4sTrackService(trackApiUri: TrackApiUri, httpClient: H4sClient) extends TrackModule.Service {
  import H4sClientDsl._

  override def getTrack(req: GetTrackRequest): Task[GetTrackResponse.Success] =
    for {
      trackUri <- eitherStringToTask(trackUri(trackApiUri, req.trackId))
      h4sUri = Uri.fromString(trackUri)
      get = GET(_: Uri, authorizationBearerHeader(req.accessToken))
      resp <- doRequest[GetTrackResponse](httpClient, h4sUri)(get)

      task <- resp match {
        case s: GetTrackResponse.Success =>
          Task.succeed(s)
        case GetTrackResponse.Error(status, message) =>
          Task.fail(new Exception(show"Error in GetTrack: status=$status, message='$message'"))
      }
    } yield task
}
