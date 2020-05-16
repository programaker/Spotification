package spotification.application

import eu.timepit.refined.auto._
import spotification.domain.spotify.authorization.AccessToken
import spotification.domain.spotify.playlist.{GetPlaylistsItemsRequest, GetPlaylistsItemsResponse}
import spotification.domain.spotify.playlist.GetPlaylistsItemsRequest.{FirstRequest, NextRequest}
import spotification.infra.config.PlaylistConfigModule
import spotification.infra.spotify.playlist.PlaylistModule
import zio.RIO
import cats.implicits._
import spotification.domain.spotify.album.AlbumType

object ReleaseRadarApp {
  val fillReleaseRadarNoSinglesProgram: RIO[ReleaseRadarAppEnv, Unit] = for {
    accessToken    <- SpotifyAuthorizationApp.requestAccessTokenProgram
    playlistConfig <- PlaylistConfigModule.config

    req = FirstRequest(
      accessToken = accessToken,
      playlistId = playlistConfig.releaseRadarId,
      fields = "next,total,items.track.album(id,album_type)",
      limit = 100,
      offset = 0
    )

    _ <- fillReleaseRadarNoSingles(accessToken, req)
  } yield ()

  private def fillReleaseRadarNoSingles(
    accessToken: AccessToken,
    req: GetPlaylistsItemsRequest
  ): RIO[ReleaseRadarAppEnv, Unit] =
    PlaylistModule.getPlaylistItems(req).flatMap {
      case GetPlaylistsItemsResponse.Success(items, _, next) =>
        for {
          albums <- RIO.succeed(items.filter(track => AlbumType.isAlbum(track.album.album_type)))
          //TODO => do something with the albums
          _ <- next match {
            case Some(nextUri) => fillReleaseRadarNoSingles(accessToken, NextRequest(accessToken, nextUri))
            case None          => RIO.succeed(())
          }
        } yield ()

      case GetPlaylistsItemsResponse.Error(status, message) =>
        RIO.fail(new Exception(show"Error in GetPlaylistItems: status=$status, message='$message'"))
    }
}
