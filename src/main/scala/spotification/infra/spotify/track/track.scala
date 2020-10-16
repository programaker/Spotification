package spotification.infra.spotify

import spotification.domain.config.TrackConfig
import spotification.domain.spotify.track._
import spotification.infra.config.TrackConfigModule
import spotification.infra.httpclient._
import zio._

package object track {
  type TrackModule = Has[TrackService]
  object TrackModule {
    val live: TaskLayer[TrackModule] = {
      val l1 = ZLayer.fromServices[TrackConfig, H4sClient, TrackService] { (config, httpClient) =>
        new H4sTrackService(config.trackApiUri, httpClient)
      }

      (TrackConfigModule.live ++ HttpClientModule.live) >>> l1
    }
  }

  def getTrack(req: GetTrackRequest): RIO[TrackModule, GetTrackResponse] =
    ZIO.accessM(_.get.getTrack(req))
}
