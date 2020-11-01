package spotification.track.infra

import org.http4s.Method.GET
import org.http4s.Uri
import eu.timepit.refined.auto._
import io.circe.generic.auto._
import spotification.common.application.leftStringEitherToTask
import spotification.common.infra.httpclient.{H4sClient, authorizationBearerHeader, doRequest}
import spotification.track.{TrackApiUri, trackUri}
import spotification.track.application.TrackService
import spotification.track._
import zio.Task
import zio.interop.catz.monadErrorInstance

final class H4sTrackService(trackApiUri: TrackApiUri, httpClient: H4sClient) extends TrackService {
  import H4sClient.Dsl._

  override def getTrack(req: GetTrackRequest): Task[GetTrackResponse] =
    for {
      trackUri <- leftStringEitherToTask(trackUri(trackApiUri, req.trackId))
      h4sUri   <- Task.fromEither(Uri.fromString(trackUri))
      resp     <- doRequest[GetTrackResponse](httpClient, h4sUri)(GET(_, authorizationBearerHeader(req.accessToken)))
    } yield resp
}