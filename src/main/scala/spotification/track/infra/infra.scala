package spotification.track

import spotification.common.infra.httpclient.{H4sClient, HttpClientModule}
import spotification.config.TrackConfig
import spotification.config.application.TrackConfigEnv
import spotification.authorization.application.spotifyauthorizarion.SpotifyAuthorizationEnv
import spotification.track.application.{ShareTrackEnv, TrackService, TrackServiceEnv}
import zio.{TaskLayer, ZLayer}

package object infra {
  val TrackServiceLayer: TaskLayer[TrackServiceEnv] = {
    val l1 = ZLayer.fromServices[TrackConfig, H4sClient, TrackService] { (config, httpClient) =>
      new H4sTrackService(config.trackApiUri, httpClient)
    }

    (TrackConfigModule.live ++ HttpClientModule.live) >>> l1
  }

  val ShareTrackLayer: TaskLayer[ShareTrackEnv] =
    TrackServiceLayer ++ SpotifyAuthorizationEnv.SpotifyAuthorizationLayer
}
