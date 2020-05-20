package spotification.application

import cats.implicits._
import eu.timepit.refined.auto._
import spotification.domain.spotify.album._
import spotification.domain.spotify.authorization.AccessToken
import spotification.domain.spotify.playlist.GetPlaylistsItemsRequest.{FirstRequest, NextRequest}
import spotification.domain.spotify.playlist.GetPlaylistsItemsResponse.Success.TrackResponse
import spotification.domain.spotify.playlist._
import spotification.infra.config.PlaylistConfigModule
import spotification.infra.spotify.playlist.PlaylistModule
import zio.RIO

object ReleaseRadarApp {
  val fillReleaseRadarNoSinglesProgram: RIO[ReleaseRadarAppEnv, Unit] = for {
    accessToken    <- SpotifyAuthorizationApp.requestAccessTokenProgram
    playlistConfig <- PlaylistConfigModule.config

    req = FirstRequest(
      accessToken = accessToken,
      playlistId = playlistConfig.releaseRadarId,
      fields = "next,total,items.track.album(id,album_type)",
      limit = playlistConfig.getPlaylistItemsLimit,
      offset = 0
    )

    _ <- fillReleaseRadarNoSingles(accessToken, req, playlistConfig.releaseRadarNoSinglesId)
  } yield ()

  private def fillReleaseRadarNoSingles(
    accessToken: AccessToken,
    req: GetPlaylistsItemsRequest,
    destPlaylist: PlaylistId
  ): RIO[ReleaseRadarAppEnv, Unit] =
    PlaylistModule.getPlaylistItems(req).flatMap {
      case GetPlaylistsItemsResponse.Success(items, _, nextPageUri) =>
        val importAlbums = AlbumImport.importAlbums(accessToken, items.mapFilter(extractAlbumId), destPlaylist)

        val nextPage = nextPageUri match {
          case Some(uri) => fillReleaseRadarNoSingles(accessToken, NextRequest(accessToken, uri), destPlaylist)
          case None      => RIO.succeed(())
        }

        importAlbums zipParRight nextPage

      case GetPlaylistsItemsResponse.Error(status, message) =>
        RIO.fail(new Exception(show"Error in GetPlaylistItems: status=$status, message='$message'"))
    }

  private def extractAlbumId(track: TrackResponse): Option[AlbumId] =
    if (AlbumType.isAlbum(track.album.album_type)) Some(track.album.id)
    else None
}
