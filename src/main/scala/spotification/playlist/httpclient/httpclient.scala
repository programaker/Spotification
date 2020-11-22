package spotification.playlist

import spotification.common.httpclient.{H4sClient, HttpClientLayer}
import spotification.config.{PlaylistConfig, UserConfig}
import spotification.config.source.{PlaylistConfigLayer, UserConfigLayer}
import spotification.playlist.service.{PlaylistService, PlaylistServiceEnv}
import zio._

package object httpclient {
  val PlaylistServiceLayer: TaskLayer[PlaylistServiceEnv] = {
    val l1 = ZLayer.fromServices[PlaylistConfig, UserConfig, H4sClient, PlaylistService] {
      (plConfig, uConfig, httpClient) =>
        new H4sPlaylistService(plConfig.playlistApiUri, uConfig.userApiUri, httpClient)
    }

    (PlaylistConfigLayer ++ UserConfigLayer ++ HttpClientLayer) >>> l1
  }
}
