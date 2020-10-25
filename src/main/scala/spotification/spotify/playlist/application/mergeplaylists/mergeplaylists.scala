package spotification.application

import cats.data.NonEmptyList
import cats.implicits._
import eu.timepit.refined.auto._
import spotification.spotify.authorization.application.spotifyauthorization.{
  SpotifyAuthorizationEnv,
  requestAccessTokenProgram
}
import spotification.config.RetryConfig
import spotification.spotify.authorization.RefreshToken
import spotification.spotify.playlist.GetPlaylistsItemsRequest.FirstRequest
import spotification.spotify.playlist.PlaylistId
import spotification.infra.config.{PlaylistConfigModule, playlistConfig}
import spotification.log.LogModule
import spotification.log.info
import spotification.infra.spotify.playlist.PlaylistModule
import zio.clock.Clock
import zio.duration.Duration
import zio.{RIO, Schedule, TaskLayer, ZIO}

package object mergeplaylists {
  type MergePlaylistsEnv = Clock
    with LogModule
    with PlaylistModule
    with PlaylistConfigModule
    with SpotifyAuthorizationEnv
  object MergePlaylistsEnv {
    val live: TaskLayer[MergePlaylistsEnv] =
      Clock.live ++
        LogModule.live ++
        PlaylistModule.live ++
        PlaylistConfigModule.live ++
        SpotifyAuthorizationEnv.live
  }

  def mergePlaylistsProgram(
    refreshToken: RefreshToken,
    mergedPlaylistId: PlaylistId,
    playlistsToMerge: List[PlaylistId]
  ): RIO[MergePlaylistsEnv, Unit] =
    for {
      accessToken    <- requestAccessTokenProgram(refreshToken)
      playlistConfig <- playlistConfig

      limit = playlistConfig.getPlaylistItemsLimit
      firstRequest = FirstRequest.make(_, limit, accessToken)
      retry = playlistConfig.mergePlaylistsRetry

      _ <- info(show"Cleaning up merged-playlist($mergedPlaylistId)")
      _ <- clearPlaylist(firstRequest(mergedPlaylistId))

      _ <- info(show"Feeding merged-playlist using $playlistsToMerge")
      _ <- mergePlaylists(playlistsToMerge, mergedPlaylistId, firstRequest, retry)

      _ <- info("Done!")
    } yield ()

  private def mergePlaylists(
    sources: List[PlaylistId],
    dest: PlaylistId,
    mkReq: PlaylistId => FirstRequest,
    retry: RetryConfig
  ): RIO[MergePlaylistsEnv, Unit] =
    NonEmptyList
      .fromList(sources)
      .map { playlists =>
        ZIO.foreachPar_(playlists.toIterable) { playlist =>
          info(show"> playlist($playlist) is being imported") *>
            importPlaylist(mkReq(playlist), dest, retry) *>
            info(show"< playlist($playlist) done")
        }
      }
      .getOrElse(RIO.unit)

  private def importPlaylist(
    source: FirstRequest,
    dest: PlaylistId,
    retry: RetryConfig
  ): RIO[MergePlaylistsEnv, Unit] =
    paginatePlaylistPar(source) { tracks =>
      importTracks(tracks.map(_.uri), dest, source.accessToken)
    }.retry(Schedule.exponential(Duration.fromScala(retry.retryAfter)) && Schedule.recurs(retry.attempts))
}
