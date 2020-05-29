package spotification.application

import cats.data.NonEmptyList
import cats.implicits._
import spotification.application.TrackImport.importTracks
import spotification.domain.spotify.playlist.GetPlaylistsItemsRequest.FirstRequest
import spotification.domain.spotify.playlist.PlaylistId
import spotification.infra.config.PlaylistConfigModule
import spotification.infra.log.LogModule._
import zio.{RIO, ZIO}

object MergePlaylistsApp {
  private val unit: RIO[MergePlaylistsEnv, Unit] = RIO.unit

  val mergePlaylistsProgram: RIO[MergePlaylistsEnv, Unit] =
    for {
      accessToken    <- SpotifyAuthorizationApp.requestAccessTokenProgram
      playlistConfig <- PlaylistConfigModule.config

      mergedPlaylistId = playlistConfig.mergedPlaylistId
      playlistsToMerge = playlistConfig.playlistsToMerge
      limit = playlistConfig.getPlaylistItemsLimit
      firstRequest = FirstRequest.make(accessToken, _, limit)

      _ <- info(show"Cleaning up merged-playlist($mergedPlaylistId)")
      _ <- PlaylistCleanUp.clearPlaylist(firstRequest(mergedPlaylistId))

      _ <- info(show"Feeding merged-playlist using $playlistsToMerge")
      _ <- mergePlaylists(playlistsToMerge, mergedPlaylistId, firstRequest)

      _ <- info("Done!")
    } yield ()

  private def mergePlaylists(
    sources: List[PlaylistId],
    dest: PlaylistId,
    mkReq: PlaylistId => FirstRequest
  ): RIO[MergePlaylistsEnv, Unit] =
    NonEmptyList.fromList(sources).fold(unit) { playlists =>
      ZIO.foreachPar_(playlists.toIterable) { playlist =>
        info(show"> playlist($playlist) is being imported") *>
          importPlaylist(mkReq(playlist), dest) *>
          info(show"< playlist($playlist) done")
      }
    }

  private def importPlaylist(source: FirstRequest, dest: PlaylistId): RIO[MergePlaylistsEnv, Unit] =
    PlaylistPagination.foreachPagePar(source) { tracks =>
      importTracks(tracks.map(_.uri), dest, source.accessToken)
    }
}
