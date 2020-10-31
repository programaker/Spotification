package spotification.spotify.playlist

import spotification.common.infra.httpclient.{H4sClient, HttpClientModule}
import spotification.config.PlaylistConfig
import spotification.config.application.PlaylistConfigModule
import spotification.log.application.LogModule
import spotification.spotify.authorization.application.spotifyauthorizarion.SpotifyAuthorizationEnv
import spotification.spotify.playlist.application.{
  MergePlaylistsEnv,
  PlaylistService,
  PlaylistServiceEnv,
  ReleaseRadarNoSinglesEnv
}
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
      SpotifyAuthorizationEnv.live

  val MergePlaylistsLayer: TaskLayer[MergePlaylistsEnv] =
    Clock.live ++
      LogModule.live ++
      PlaylistServiceLayer ++
      PlaylistConfigModule.live ++
      SpotifyAuthorizationEnv.live
}
