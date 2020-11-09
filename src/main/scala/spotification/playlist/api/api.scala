package spotification.playlist

import spotification.authorization.api.SpotifyAuthorizationLayer
import spotification.config.source.PlaylistConfigLayer
import spotification.log.impl.LogLayer
import spotification.playlist.httpclient.PlaylistServiceLayer
import spotification.playlist.program.{MergePlaylistsEnv, ReleaseRadarNoSinglesEnv}
import zio.TaskLayer
import zio.clock.Clock

package object api {
  val ReleaseRadarNoSinglesLayer: TaskLayer[ReleaseRadarNoSinglesEnv] =
    LogLayer ++
      PlaylistServiceLayer ++
      PlaylistConfigLayer ++
      SpotifyAuthorizationLayer

  val MergePlaylistsLayer: TaskLayer[MergePlaylistsEnv] =
    Clock.live ++
      LogLayer ++
      PlaylistServiceLayer ++
      PlaylistConfigLayer ++
      SpotifyAuthorizationLayer
}
