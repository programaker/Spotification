package spotification.infra.spotify

import eu.timepit.refined.auto._
import io.circe.generic.auto._
import org.http4s.Method.GET
import org.http4s.Uri
import spotification.domain.config.TrackConfig
import spotification.domain.spotify.track.Track.trackUri
import spotification.domain.spotify.track._
import spotification.infra.Infra.leftStringEitherToTask
import spotification.infra.config.TrackConfigModule
import spotification.infra.httpclient.AuthorizationHttpClient.authorizationBearerHeader
import spotification.infra.httpclient.HttpClient.{H4sClientDsl, doRequest}
import spotification.infra.httpclient._
import zio.{Task, _}
import zio.interop.catz.monadErrorInstance
import io.circe.refined._

package object track {
  type TrackModule = Has[TrackModule.Service]
  object TrackModule {
    def getTrack(req: GetTrackRequest): RIO[TrackModule, GetTrackResponse] =
      ZIO.accessM(_.get.getTrack(req))

    val layer: TaskLayer[TrackModule] = {
      val l1 = ZLayer.fromServices[TrackConfig, H4sClient, TrackModule.Service] { (config, httpClient) =>
        new H4sTrackService(config.trackApiUri, httpClient)
      }

      (TrackConfigModule.layer ++ HttpClientModule.layer) >>> l1
    }

    trait Service {
      def getTrack(req: GetTrackRequest): Task[GetTrackResponse]
    }
  }

  final private class H4sTrackService(trackApiUri: TrackApiUri, httpClient: H4sClient) extends TrackModule.Service {
    import H4sClientDsl._

    override def getTrack(req: GetTrackRequest): Task[GetTrackResponse] =
      for {
        trackUri <- leftStringEitherToTask(trackUri(trackApiUri, req.trackId))
        h4sUri   <- Task.fromEither(Uri.fromString(trackUri))
        resp     <- doRequest[GetTrackResponse](httpClient, h4sUri)(GET(_, authorizationBearerHeader(req.accessToken)))
      } yield resp
  }
}
