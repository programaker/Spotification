package spotification.track

import spotification.common.httpclient.{H4sClient, HttpClientLayer}
import spotification.config.TrackConfig
import spotification.config.source.TrackConfigLayer
import spotification.track.service.{TrackService, TrackServiceEnv}
import zio.{TaskLayer, ZLayer}

package object httpclient {
  val TrackServiceLayer: TaskLayer[TrackServiceEnv] = {
    val l1 = ZLayer.fromServices[TrackConfig, H4sClient, TrackService] { (config, httpClient) =>
      new H4sTrackService(config.trackApiUri, httpClient)
    }

    (TrackConfigLayer ++ HttpClientLayer) >>> l1
  }
}
