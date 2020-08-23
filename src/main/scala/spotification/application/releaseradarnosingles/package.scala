package spotification.application

import cats.data.NonEmptyList
import cats.implicits._
import spotification.infra.log.LogModule
import spotification.infra.spotify.playlist.PlaylistModule
import spotification.application.spotifyauthorization.{SpotifyAuthorizationEnv, requestAccessTokenProgram}
import spotification.domain.spotify.authorization.RefreshToken
import spotification.domain.spotify.playlist.GetPlaylistsItemsRequest.FirstRequest
import spotification.domain.spotify.playlist.{PlaylistId, trackUriIfAlbum}
import spotification.infra.config.PlaylistConfigModule
import spotification.infra.log.LogModule.info
import zio.{RIO, TaskLayer}

package object releaseradarnosingles {
  type ReleaseRadarNoSinglesEnv = LogModule with PlaylistModule with PlaylistConfigModule with SpotifyAuthorizationEnv
  object ReleaseRadarNoSinglesEnv {
    val layer: TaskLayer[ReleaseRadarNoSinglesEnv] =
      LogModule.layer ++
        PlaylistModule.layer ++
        PlaylistConfigModule.layer ++
        SpotifyAuthorizationEnv.layer
  }

  def releaseRadarNoSinglesProgram(
    refreshToken: RefreshToken,
    releaseRadarId: PlaylistId,
    releaseRadarNoSinglesId: PlaylistId
  ): RIO[ReleaseRadarNoSinglesEnv, Unit] =
    for {
      accessToken    <- requestAccessTokenProgram(refreshToken)
      playlistConfig <- PlaylistConfigModule.config

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
