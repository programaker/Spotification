package spotification.application

import eu.timepit.refined.auto._
import spotification.domain.spotify.authorization.AccessToken
import spotification.domain.spotify.playlist.{GetPlaylistsItemsRequest, GetPlaylistsItemsResponse}
import spotification.domain.spotify.playlist.GetPlaylistsItemsRequest.{FirstRequest, NextRequest}
import spotification.infra.config.PlaylistConfigModule
import spotification.infra.spotify.playlist.PlaylistModule
import zio.{RIO, ZIO}
import cats.implicits._
import spotification.domain.spotify.album._
import spotification.domain.spotify.playlist.GetPlaylistsItemsResponse.Success.TrackResponse
import spotification.infra.Infra.refineRIO
import spotification.infra.spotify.album.AlbumModule

object ReleaseRadarApp {
  val fillReleaseRadarNoSinglesProgram: RIO[ReleaseRadarAppEnv, Unit] = for {
    accessToken    <- SpotifyAuthorizationApp.requestAccessTokenProgram
    playlistConfig <- PlaylistConfigModule.config

    req = FirstRequest(
      accessToken = accessToken,
      playlistId = playlistConfig.releaseRadarId,
      fields = "next,total,items.track.album(id,album_type)",
      limit = 100, //TODO => move this to PlaylistConfig
      offset = 0
    )

    _ <- fillReleaseRadarNoSingles(accessToken, req)
  } yield ()

  private def fillReleaseRadarNoSingles(
    accessToken: AccessToken,
    req: GetPlaylistsItemsRequest
  ): RIO[ReleaseRadarAppEnv, Unit] =
    PlaylistModule.getPlaylistItems(req).flatMap {
      case GetPlaylistsItemsResponse.Success(items, _, nextPageUri) =>
        val albums = importAlbums(accessToken, items)

        val nextPage = nextPageUri match {
          case Some(uri) => fillReleaseRadarNoSingles(accessToken, NextRequest(accessToken, uri))
          case None      => RIO.succeed(())
        }

        albums zipParRight nextPage

      case GetPlaylistsItemsResponse.Error(status, message) =>
        RIO.fail(new Exception(show"Error in GetPlaylistItems: status=$status, message='$message'"))
    }

  private def importAlbums(accessToken: AccessToken, items: List[TrackResponse]): RIO[ReleaseRadarAppEnv, Unit] = {
    val allAlbumChunks = items
      .to(LazyList)
      .mapFilter(extractAlbumId)
      .grouped(AlbumIdsToGet.MaxSize)
      .map(_.toVector)
      .map(refineRIO[ReleaseRadarAppEnv, AlbumIdsToGetR](_))
      .map(_.flatMap(importAlbumChunk(accessToken, _)))
      .to(Iterable)

    ZIO.foreachPar_(allAlbumChunks)(identity)
  }

  private def importAlbumChunk(accessToken: AccessToken, albumIds: AlbumIdsToGet): RIO[ReleaseRadarAppEnv, Unit] =
    AlbumModule.getSeveralAlbums(GetSeveralAlbumsRequest(accessToken, albumIds)).flatMap {
      case GetSeveralAlbumsResponse.Success(albums) =>
        RIO.succeed(())

      case GetSeveralAlbumsResponse.Error(status, message) =>
        RIO.fail(new Exception(show"Error in GetSeveralAlbums: status=$status, message='$message'"))
    }

  private def extractAlbumId(track: TrackResponse): Option[AlbumId] =
    if (AlbumType.isAlbum(track.album.album_type)) Some(track.album.id)
    else None
}
