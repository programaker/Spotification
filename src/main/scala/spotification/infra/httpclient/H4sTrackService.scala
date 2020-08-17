package spotification.infra.httpclient

import eu.timepit.refined.auto._
import org.http4s.Method.GET
import org.http4s.Uri
import spotification.domain.spotify.track.Track.trackUri
import spotification.domain.spotify.track._
import spotification.infra.Infra.leftStringEitherToTask
import spotification.infra.Json.Implicits.{ErrorResponseDecoder, GetTrackResponseDecoder}
import spotification.infra.httpclient.AuthorizationHttpClient.authorizationBearerHeader
import spotification.infra.httpclient.HttpClient.{H4sClientDsl, doRequest}
import spotification.infra.spotify.track.TrackModule
import zio.Task
import zio.interop.catz.monadErrorInstance

final class H4sTrackService(trackApiUri: TrackApiUri, httpClient: H4sClient) extends TrackModule.Service {
  import H4sClientDsl._

  override def getTrack(req: GetTrackRequest): Task[GetTrackResponse] =
    for {
      trackUri <- leftStringEitherToTask(trackUri(trackApiUri, req.trackId))
      h4sUri   <- Task.fromEither(Uri.fromString(trackUri))
      resp     <- doRequest[GetTrackResponse](httpClient, h4sUri)(GET(_, authorizationBearerHeader(req.accessToken)))
    } yield resp
}
