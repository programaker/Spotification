package spotification.application

import cats.data.NonEmptyList
import cats.implicits._
import spotification.domain.spotify.album._
import spotification.domain.spotify.playlist.GetPlaylistsItemsResponse.Success.TrackResponse
import spotification.domain.spotify.track.TrackUri
import spotification.infra.config.PlaylistConfigModule
import spotification.infra.spotify.playlist.PlaylistModule
import zio.{RIO, ZIO}

object ReleaseRadarApp {
  val fillReleaseRadarNoSinglesProgram: RIO[ReleaseRadarAppEnv, Unit] =
    for {
      accessToken    <- SpotifyAuthorizationApp.requestAccessTokenProgram
      playlistConfig <- PlaylistConfigModule.config

      releaseRadar = playlistConfig.releaseRadarId
      releaseRadarNoSingles = playlistConfig.releaseRadarNoSinglesId
      limit = playlistConfig.getPlaylistItemsLimit

      _ <- PlaylistCleanUp.clearPlaylist(releaseRadarNoSingles, accessToken, limit)

      /*_ <- PlaylistPagination.foreachPage(releaseRadar, limit, accessToken) { tracks =>
        //RIO.succeed(tracks.map(_.uri)).flatMap(uris => ZIO.succeed(println(show">>> ${uris.size}")))
      val trackUris = tracks.mapFilter(trackUriIfAlbum)
        val ifEmpty: RIO[PlaylistModule, Unit] = RIO.unit
        val importTracks = TrackImport.importTracks(_, releaseRadarNoSingles, accessToken)
        NonEmptyList.fromList(trackUris).fold(ifEmpty)(importTracks)
      }*/
    } yield ()

  private def trackUriIfAlbum(track: TrackResponse): Option[TrackUri] =
    if (AlbumType.isAlbum(track.album.album_type)) Some(track.uri)
    else None
}
