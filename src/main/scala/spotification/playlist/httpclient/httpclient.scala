package spotification.playlist

import spotification.common.httpclient.{H4sClient, HttpClientLayer}
import spotification.config.PlaylistConfig
import spotification.config.source.PlaylistConfigLayer
import spotification.playlist.service.{PlaylistService, PlaylistServiceEnv}
import zio._

package object httpclient {
  val PlaylistServiceLayer: TaskLayer[PlaylistServiceEnv] = {
    val l1 = ZLayer.fromServices[PlaylistConfig, H4sClient, PlaylistService] { (playlistConfig, httpClient) =>
      new H4sPlaylistService(playlistConfig.playlistApiUri, httpClient)
    }

    (PlaylistConfigLayer ++ HttpClientLayer) >>> l1
  }
}
