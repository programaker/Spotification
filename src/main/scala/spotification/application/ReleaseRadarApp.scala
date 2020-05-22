package spotification.application

import cats.data.NonEmptyList
import cats.implicits._
import eu.timepit.refined.auto._
import spotification.domain.spotify.album._
import spotification.domain.spotify.playlist.GetPlaylistsItemsRequest.{FirstRequest, NextRequest}
import spotification.domain.spotify.playlist.GetPlaylistsItemsResponse.Success.TrackResponse
import spotification.domain.spotify.playlist._
import spotification.domain.spotify.track.TrackUri
import spotification.infra.config.PlaylistConfigModule
import spotification.infra.spotify.playlist.PlaylistModule
import zio.RIO

object ReleaseRadarApp {
  val fillReleaseRadarNoSinglesProgram: RIO[ReleaseRadarAppEnv, Unit] =
    for {
      accessToken    <- SpotifyAuthorizationApp.requestAccessTokenProgram
      playlistConfig <- PlaylistConfigModule.config
      _              <- PlaylistCleanUp.clearPlaylist(playlistConfig.releaseRadarNoSinglesId, accessToken)

      req = FirstRequest(
        accessToken = accessToken,
        playlistId = playlistConfig.releaseRadarId,
        limit = playlistConfig.getPlaylistItemsLimit,
        offset = 0
      )

      _ <- fillReleaseRadarNoSingles(req, playlistConfig.releaseRadarNoSinglesId)
    } yield ()

  private def fillReleaseRadarNoSingles(
    req: GetPlaylistsItemsRequest,
    destPlaylist: PlaylistId
  ): RIO[ReleaseRadarAppEnv, Unit] =
    PlaylistModule.getPlaylistItems(req).flatMap { resp =>
      val ifEmpty: RIO[ReleaseRadarAppEnv, Unit] = RIO.unit
      val accessToken = GetPlaylistsItemsRequest.accessToken(req)

      NonEmptyList.fromList(resp.items.mapFilter(trackUriIfAlbum)).fold(ifEmpty) { trackUris =>
        val importTracks = TrackImport.importTracks(trackUris, accessToken, destPlaylist)

        val nextPage = resp.next match {
          case None      => RIO.unit
          case Some(uri) => fillReleaseRadarNoSingles(NextRequest(accessToken, uri), destPlaylist)
        }

        importTracks.zipParRight(nextPage)
      }
    }

  private def trackUriIfAlbum(track: TrackResponse): Option[TrackUri] =
    if (AlbumType.isAlbum(track.album.album_type)) Some(track.uri)
    else None
}
