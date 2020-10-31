package spotification.spotify.track.infra

import org.http4s.Method.GET
import org.http4s.Uri
import eu.timepit.refined.auto._
import spotification.common.application.leftStringEitherToTask
import spotification.common.infra.httpclient.{H4sClient, authorizationBearerHeader, doRequest}
import spotification.spotify.track.application.TrackService
import spotification.spotify.track.{trackUri, _}
import zio.Task

final class H4sTrackService(trackApiUri: TrackApiUri, httpClient: H4sClient) extends TrackService {

  override def getTrack(req: GetTrackRequest): Task[GetTrackResponse] =
    for {
      trackUri <- leftStringEitherToTask(trackUri(trackApiUri, req.trackId))
      h4sUri   <- Task.fromEither(Uri.fromString(trackUri))
      resp     <- doRequest[GetTrackResponse](httpClient, h4sUri)(GET(_, authorizationBearerHeader(req.accessToken)))
    } yield resp
}
