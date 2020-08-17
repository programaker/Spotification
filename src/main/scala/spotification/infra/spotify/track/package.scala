package spotification.infra.spotify

import spotification.domain.config.TrackConfig
import spotification.domain.spotify.track.{GetTrackRequest, GetTrackResponse}
import spotification.infra.config.TrackConfigModule
import spotification.infra.httpclient._
import zio._

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
}
