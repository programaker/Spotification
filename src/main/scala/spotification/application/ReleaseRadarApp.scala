package spotification.application

import cats.data.NonEmptyList
import cats.implicits._
import spotification.domain.spotify.album._
import spotification.domain.spotify.playlist.GetPlaylistsItemsResponse.Success.TrackResponse
import spotification.domain.spotify.track.TrackUri
import spotification.infra.config.PlaylistConfigModule
import spotification.infra.spotify.playlist.PlaylistModule
import spotification.domain.spotify.playlist.GetPlaylistsItemsRequest.FirstRequest
import eu.timepit.refined.auto._
import spotification.application.TrackImport.importTracks
import zio.RIO

object ReleaseRadarApp {
  val fillReleaseRadarNoSinglesProgram: RIO[ReleaseRadarAppEnv, Unit] =
    for {
      accessToken    <- SpotifyAuthorizationApp.requestAccessTokenProgram
      playlistConfig <- PlaylistConfigModule.config

      releaseRadar = playlistConfig.releaseRadarId
      releaseRadarNoSingles = playlistConfig.releaseRadarNoSinglesId
      limit = playlistConfig.getPlaylistItemsLimit
      firstRequest = FirstRequest.make(accessToken, _, limit)

      _ <- PlaylistCleanUp.clearPlaylist(firstRequest(releaseRadarNoSingles))

      _ <- PlaylistPagination.foreachPagePar(firstRequest(releaseRadar)) { tracks =>
        val trackUris = tracks.toList.mapFilter(trackUriIfAlbum)
        val unit: RIO[PlaylistModule, Unit] = RIO.unit
        NonEmptyList.fromList(trackUris).fold(unit)(importTracks(_, releaseRadarNoSingles, accessToken))
      }
    } yield ()

  private def trackUriIfAlbum(track: TrackResponse): Option[TrackUri] =
    if (AlbumType.isAlbum(track.album.album_type)) Some(track.uri)
    else None
}
