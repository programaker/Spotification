package spotification.application

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
    albumIds: List[AlbumId],
    accessToken: AccessToken,
    destPlaylist: PlaylistId
  ): RIO[AlbumModule with PlaylistModule, Unit] =
    ZIO.foreachPar_ {
      albumIds
        .to(LazyList)
        .grouped(AlbumIdsToGet.MaxSize)
        .map(_.toVector)
        .map(refineRIO[AlbumModule, AlbumIdsToGetR](_))
        .map(_.flatMap(importAlbumChunk(_, accessToken, destPlaylist)))
        .to(Iterable)
    }(identity)

  private def importAlbumChunk(
    albumIds: AlbumIdsToGet,
    accessToken: AccessToken,
    destPlaylist: PlaylistId
  ): RIO[AlbumModule with PlaylistModule, Unit] =
    AlbumModule.getSeveralAlbums(GetSeveralAlbumsRequest(accessToken, albumIds)).flatMap { resp =>
      val trackUris = resp.albums.map(extractTrackUris).to(Iterable)
      val importTracks = TrackImport.importTracks(_, accessToken, destPlaylist)
      ZIO.foreachPar_(trackUris)(importTracks)
    }

  private def extractTrackUris(album: GetSeveralAlbumsResponse.Success.AlbumResponse): List[TrackUri] =
    album.tracks.items.map(_.uri)
}
