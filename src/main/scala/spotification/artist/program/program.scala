package spotification.artist

import spotification.artist.GetArtistsAlbumsResponse.Album
import spotification.artist.service.{
  GetArtistsAlbumsServiceR,
  GetMyFollowedArtistsServiceR,
  getArtistsAlbums,
  getMyFollowedArtists
}
import spotification.common.program.{PageRIO, paginate}
import spotification.effect.unitRIO
import zio.RIO

package object program {
  def paginateMyFollowedArtistsPar[R <: GetMyFollowedArtistsServiceR](req: GetMyFollowedArtistsRequest[_])(
    f: List[ArtistId] => RIO[R, Unit]
  ): RIO[R, Unit] =
    paginate(unitRIO[R])(fetchMyFollowedArtistsPage[R])(f)(_ &> _)(req)

  def paginateArtistsAlbumsPar[R <: GetArtistsAlbumsServiceR](req: GetArtistsAlbumsRequest[_])(
    f: List[Album] => RIO[R, Unit]
  ): RIO[R, Unit] =
    paginate(unitRIO[R])(fetchArtistsAlbumsPage[R])(f)(_ &> _)(req)

  def fetchMyFollowedArtistsPage[R <: GetMyFollowedArtistsServiceR](
    req: GetMyFollowedArtistsRequest[_]
  ): PageRIO[R, ArtistId, GetMyFollowedArtistsRequest[_]] =
    getMyFollowedArtists(req).map(getMyFollowedArtistsPage(req, _))

  def fetchArtistsAlbumsPage[R <: GetArtistsAlbumsServiceR](
    req: GetArtistsAlbumsRequest[_]
  ): PageRIO[R, Album, GetArtistsAlbumsRequest[_]] =
    getArtistsAlbums(req).map(getArtistsAlbumsPage(req, _))
}
