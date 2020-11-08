package spotification.httpclient

import eu.timepit.refined.auto._
import io.circe.generic.auto._
import org.http4s.Method.GET
import org.http4s.Uri
import spotification.core.track.service.TrackService
import spotification.core.track.{makeTrackUri, _}
import spotification.core.util.leftStringEitherToTask
import zio.Task
import zio.interop.catz.monadErrorInstance
import io.circe.refined._

final class H4sTrackService(trackApiUri: TrackApiUri, httpClient: H4sClient) extends TrackService {
  import H4sClient.Dsl._

  override def getTrack(req: GetTrackRequest): Task[GetTrackResponse] =
    for {
      trackUri <- leftStringEitherToTask(makeTrackUri(trackApiUri, req.trackId))
      h4sUri   <- Task.fromEither(Uri.fromString(trackUri))
      resp     <- doRequest[GetTrackResponse](httpClient, h4sUri)(GET(_, authorizationBearerHeader(req.accessToken)))
    } yield resp
}
