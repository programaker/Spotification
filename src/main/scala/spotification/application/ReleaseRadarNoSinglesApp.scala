package spotification.application

import cats.data.NonEmptyList
import cats.implicits._
import spotification.domain.spotify.album._
import spotification.domain.spotify.playlist.GetPlaylistsItemsResponse.Success.TrackResponse
import spotification.domain.spotify.track.TrackUri
import spotification.infra.config.PlaylistConfigModule
import spotification.domain.spotify.playlist.GetPlaylistsItemsRequest.FirstRequest
import spotification.application.TrackImport.importTracks
import spotification.domain.spotify.playlist.PlaylistId
import zio.RIO
import spotification.infra.log.LogModule._

object ReleaseRadarNoSinglesApp {
  private val unit: RIO[ReleaseRadarNoSinglesAppEnv, Unit] = RIO.unit

  def fillReleaseRadarNoSinglesProgram(
    releaseRadarId: PlaylistId,
    releaseRadarNoSinglesId: PlaylistId
  ): RIO[ReleaseRadarNoSinglesAppEnv, Unit] =
    for {
      accessToken    <- SpotifyAuthorizationApp.requestAccessTokenProgram
      playlistConfig <- PlaylistConfigModule.config

      limit = playlistConfig.getPlaylistItemsLimit
      firstRequest = FirstRequest.make(_, limit, accessToken)

      _ <- info(show"Cleaning up release-radar-no-singles($releaseRadarNoSinglesId)")
      _ <- PlaylistCleanUp.clearPlaylist(firstRequest(releaseRadarNoSinglesId))

      _ <- info(show"Feeding release-radar-no-singles using release-radar($releaseRadarId)")
      _ <- PlaylistPagination.foreachPagePar(firstRequest(releaseRadarId)) { tracks =>
        val trackUris = tracks.toList.mapFilter(trackUriIfAlbum)
        NonEmptyList.fromList(trackUris).fold(unit)(importTracks(_, releaseRadarNoSinglesId, accessToken))
      }

      _ <- info("Done!")
    } yield ()

  private def trackUriIfAlbum(track: TrackResponse): Option[TrackUri] =
    if (AlbumType.isAlbum(track.album.album_type)) Some(track.uri)
    else None
}
