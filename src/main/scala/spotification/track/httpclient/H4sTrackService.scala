package spotification.track.httpclient

import eu.timepit.refined.auto._
import org.http4s.Method.GET
import org.http4s.Uri
import spotification.authorization.httpclient.authorizationBearerHeader
import spotification.common.httpclient.{H4sClient, doRequest}
import spotification.common.json.implicits.ErrorResponseDecoder
import spotification.effect.leftStringEitherToTask
import spotification.track.json.implicits.GetTrackResponseDecoder
import spotification.track.service.TrackService
import spotification.track.{GetTrackRequest, GetTrackResponse, TrackApiUri, makeTrackUri}
import zio.Task
import zio.interop.catz.monadErrorInstance

final class H4sTrackService(trackApiUri: TrackApiUri, httpClient: H4sClient) extends TrackService {
  import H4sClient.Dsl._

  override def getTrack(req: GetTrackRequest): Task[GetTrackResponse] =
    for {
      trackUri <- leftStringEitherToTask(makeTrackUri(trackApiUri, req.trackId))
      h4sUri   <- Task.fromEither(Uri.fromString(trackUri))
      resp     <- doRequest[GetTrackResponse](httpClient, h4sUri)(GET(_, authorizationBearerHeader(req.accessToken)))
    } yield resp
}
