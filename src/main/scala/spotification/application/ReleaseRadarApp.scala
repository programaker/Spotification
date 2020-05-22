package spotification.application

import cats.implicits._
import eu.timepit.refined.auto._
import spotification.domain.spotify.album._
import spotification.domain.spotify.playlist.GetPlaylistsItemsRequest.{FirstRequest, NextRequest}
import spotification.domain.spotify.playlist.GetPlaylistsItemsResponse.Success.TrackResponse
import spotification.domain.spotify.playlist._
import spotification.infra.config.PlaylistConfigModule
import spotification.infra.spotify.playlist.PlaylistModule
import zio.RIO

object ReleaseRadarApp {
  val fillReleaseRadarNoSinglesProgram: RIO[ReleaseRadarAppEnv, Unit] =
    for {
      accessToken    <- SpotifyAuthorizationApp.requestAccessTokenProgram
      playlistConfig <- PlaylistConfigModule.config

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
      val importAlbums = AlbumImport.importAlbums(resp.items.mapFilter(extractAlbumId), req.accessToken, destPlaylist)

      val nextPage = resp.next match {
        case Some(uri) => fillReleaseRadarNoSingles(NextRequest(req.accessToken, uri), destPlaylist)
        case None      => RIO.unit
      }

      importAlbums zipParRight nextPage
    }

  private def extractAlbumId(track: TrackResponse): Option[AlbumId] =
    if (AlbumType.isAlbum(track.album.album_type)) Some(track.album.id)
    else None
}
