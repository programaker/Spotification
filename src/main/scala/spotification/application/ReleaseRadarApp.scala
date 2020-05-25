package spotification.application

import cats.data.NonEmptyList
import cats.implicits._
import spotification.domain.spotify.album._
import spotification.domain.spotify.playlist.GetPlaylistsItemsResponse.Success.TrackResponse
import spotification.domain.spotify.track.TrackUri
import spotification.infra.config.PlaylistConfigModule
import spotification.infra.spotify.playlist.PlaylistModule
import zio.{RIO, ZIO}
import spotification.domain.spotify.playlist.GetPlaylistsItemsRequest.FirstRequest
import eu.timepit.refined.auto._

object ReleaseRadarApp {
  val fillReleaseRadarNoSinglesProgram: RIO[ReleaseRadarAppEnv, Unit] =
    for {
      accessToken    <- SpotifyAuthorizationApp.requestAccessTokenProgram
      playlistConfig <- PlaylistConfigModule.config

      releaseRadar = playlistConfig.releaseRadarId
      releaseRadarNoSingles = playlistConfig.releaseRadarNoSinglesId
      limit = playlistConfig.getPlaylistItemsLimit

      _ <- PlaylistCleanUp.clearPlaylist(FirstRequest(accessToken, releaseRadarNoSingles, limit, offset = 0))

      /*_ <- PlaylistPagination.foreachPagePar(FirstRequest(accessToken, releaseRadar, limit, offset = 0)) { tracks =>
        ////RIO.succeed(tracks.map(_.uri)).flatMap(uris => ZIO.succeed(println(show">>> GOT ${uris.size} URIs")))
      //val trackUris = tracks.mapFilter(trackUriIfAlbum)
      //val ifEmpty: RIO[PlaylistModule, Unit] = RIO.unit
      //val importTracks = TrackImport.importTracks(_, releaseRadarNoSingles, accessToken)
      //NonEmptyList.fromList(trackUris).fold(ifEmpty)(importTracks)
      }*/
    } yield ()

  private def trackUriIfAlbum(track: TrackResponse): Option[TrackUri] =
    if (AlbumType.isAlbum(track.album.album_type)) Some(track.uri)
    else None
}
