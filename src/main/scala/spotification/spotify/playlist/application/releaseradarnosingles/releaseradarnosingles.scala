package spotification.spotify.playlist.application

import cats.data.NonEmptyList
import cats.implicits._
import spotification.config.application.{PlaylistConfigModule, playlistConfig}
import spotification.log.application.{LogModule, info}
import spotification.spotify.authorization.RefreshToken
import spotification.spotify.authorization.application.spotifyauthorizarion.{
  SpotifyAuthorizationEnv,
  requestAccessTokenProgram
}
import spotification.spotify.playlist.GetPlaylistsItemsRequest.FirstRequest
import spotification.spotify.playlist.{PlaylistId, trackUriIfAlbum}
import spotification.spotify.track.application.importTracks
import zio.{RIO, TaskLayer}

package object releaseradarnosingles {
  type ReleaseRadarNoSinglesEnv = LogModule with PlaylistModule with PlaylistConfigModule with SpotifyAuthorizationEnv
  object ReleaseRadarNoSinglesEnv {
    val live: TaskLayer[ReleaseRadarNoSinglesEnv] =
      LogModule.live ++
        PlaylistModule.live ++
        PlaylistConfigModule.live ++
        SpotifyAuthorizationEnv.live
  }

  def releaseRadarNoSinglesProgram(
    refreshToken: RefreshToken,
    releaseRadarId: PlaylistId,
    releaseRadarNoSinglesId: PlaylistId
  ): RIO[ReleaseRadarNoSinglesEnv, Unit] =
    for {
      accessToken    <- requestAccessTokenProgram(refreshToken)
      playlistConfig <- playlistConfig

      limit = playlistConfig.getPlaylistItemsLimit
      firstRequest = FirstRequest.make(_, limit, accessToken)

      _ <- info(show"Cleaning up release-radar-no-singles($releaseRadarNoSinglesId)")
      _ <- clearPlaylist(firstRequest(releaseRadarNoSinglesId))

      _ <- info(show"Feeding release-radar-no-singles using release-radar($releaseRadarId)")
      _ <- paginatePlaylistPar(firstRequest(releaseRadarId)) { tracks =>
        val trackUris = tracks.toList.mapFilter(trackUriIfAlbum)
        NonEmptyList.fromList(trackUris).map(importTracks(_, releaseRadarNoSinglesId, accessToken)).getOrElse(RIO.unit)
      }

      _ <- info("Done!")
    } yield ()
}
