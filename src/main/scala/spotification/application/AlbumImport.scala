package spotification.application

import cats.implicits._
import spotification.domain.spotify.album._
import spotification.domain.spotify.authorization.AccessToken
import spotification.domain.spotify.playlist.PlaylistId
import spotification.domain.spotify.track.TrackUri
import spotification.infra.Infra.refineRIO
import spotification.infra.spotify.album.AlbumModule
import spotification.infra.spotify.playlist.PlaylistModule
import zio.{RIO, ZIO}

object AlbumImport {
  def importAlbums(
    accessToken: AccessToken,
    albumIds: List[AlbumId],
    destPlaylist: PlaylistId
  ): RIO[AlbumModule with PlaylistModule, Unit] =
    ZIO.foreachPar_ {
      albumIds
        .to(LazyList)
        .grouped(AlbumIdsToGet.MaxSize)
        .map(_.toVector)
        .map(refineRIO[AlbumModule, AlbumIdsToGetR](_))
        .map(_.flatMap(importAlbumChunk(accessToken, _, destPlaylist)))
        .to(Iterable)
    }(identity)

  private def importAlbumChunk(
    accessToken: AccessToken,
    albumIds: AlbumIdsToGet,
    destPlaylist: PlaylistId
  ): RIO[AlbumModule with PlaylistModule, Unit] =
    AlbumModule.getSeveralAlbums(GetSeveralAlbumsRequest(accessToken, albumIds)).flatMap {
      case GetSeveralAlbumsResponse.Success(albums) =>
        val trackUris = albums.map(extractTrackUris).to(Iterable)
        val importTracks = TrackImport.importTracks(accessToken, _, destPlaylist)
        ZIO.foreachPar_(trackUris)(importTracks)

      case GetSeveralAlbumsResponse.Error(status, message) =>
        RIO.fail(new Exception(show"Error in GetSeveralAlbums: status=$status, message='$message'"))
    }

  private def extractTrackUris(album: GetSeveralAlbumsResponse.Success.AlbumResponse): List[TrackUri] =
    album.tracks.items.map(_.uri)
}
