package spotification.application

import cats.data.NonEmptyList
import cats.implicits._
import spotification.application.TrackImport.importTracks
import spotification.domain.config.RetryConfig
import spotification.domain.spotify.playlist.GetPlaylistsItemsRequest.FirstRequest
import spotification.domain.spotify.playlist.PlaylistId
import spotification.infra.config.PlaylistConfigModule
import spotification.infra.log.LogModule._
import zio.{RIO, Schedule, ZIO}
import zio.duration._
import eu.timepit.refined.auto._

object MergePlaylistsApp {
  private val unit: RIO[MergedPlaylistsEnv, Unit] = RIO.unit

  def mergePlaylistsProgram(
    mergedPlaylistId: PlaylistId,
    playlistsToMerge: List[PlaylistId]
  ): RIO[MergedPlaylistsEnv, Unit] =
    for {
      accessToken    <- SpotifyAuthorizationApp.requestAccessTokenProgram
      playlistConfig <- PlaylistConfigModule.config

      limit = playlistConfig.getPlaylistItemsLimit
      firstRequest = FirstRequest.make(_, limit, accessToken)
      retry = playlistConfig.mergePlaylistsRetry

      _ <- info(show"Cleaning up merged-playlist($mergedPlaylistId)")
      _ <- PlaylistCleanUp.clearPlaylist(firstRequest(mergedPlaylistId))

      _ <- info(show"Feeding merged-playlist using $playlistsToMerge")
      _ <- mergePlaylists(playlistsToMerge, mergedPlaylistId, firstRequest, retry)

      _ <- info("Done!")
    } yield ()

  private def mergePlaylists(
    sources: List[PlaylistId],
    dest: PlaylistId,
    mkReq: PlaylistId => FirstRequest,
    retry: RetryConfig
  ): RIO[MergedPlaylistsEnv, Unit] =
    NonEmptyList.fromList(sources).fold(unit) { playlists =>
      ZIO.foreachPar_(playlists.toIterable) { playlist =>
        info(show"> playlist($playlist) is being imported") *>
          importPlaylist(mkReq(playlist), dest, retry) *>
          info(show"< playlist($playlist) done")
      }
    }

  private def importPlaylist(
    source: FirstRequest,
    dest: PlaylistId,
    retry: RetryConfig
  ): RIO[MergedPlaylistsEnv, Unit] =
    PlaylistPagination
      .foreachPagePar(source) { tracks =>
        importTracks(tracks.map(_.uri), dest, source.accessToken)
      }
      .retry(Schedule.exponential(Duration.fromScala(retry.retryAfter)) && Schedule.recurs(retry.attempts))
}
