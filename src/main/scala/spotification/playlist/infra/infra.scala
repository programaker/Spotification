package spotification.playlist

import spotification.config.PlaylistConfig
import spotification.config.application.PlaylistConfigEnv
import spotification.log.application.LogModule
import spotification.authorization.infra.SpotifyAuthorizationLayer
import spotification.playlist.application.{
  MergePlaylistsEnv,
  PlaylistService,
  PlaylistServiceEnv,
  ReleaseRadarNoSinglesEnv
}
import spotification.common.infra.httpclient.H4sClient
import spotification.common.infra.httpclient.HttpClientModule
import zio.clock.Clock
import zio.{TaskLayer, ZLayer}

package object infra {
  val PlaylistServiceLayer: TaskLayer[PlaylistServiceEnv] = {
    val l1 = ZLayer.fromServices[PlaylistConfig, H4sClient, PlaylistService] { (config, httpClient) =>
      new H4sPlaylistService(config.playlistApiUri, httpClient)
    }

    (PlaylistConfigModule.live ++ HttpClientModule.live) >>> l1
  }

  val ReleaseRadarNoSinglesLayer: TaskLayer[ReleaseRadarNoSinglesEnv] =
    LogModule.live ++
      PlaylistServiceLayer ++
      PlaylistConfigModule.live ++
      SpotifyAuthorizationLayer

  val MergePlaylistsLayer: TaskLayer[MergePlaylistsEnv] =
    Clock.live ++
      LogModule.live ++
      PlaylistServiceLayer ++
      PlaylistConfigModule.live ++
      SpotifyAuthorizationLayer
}
