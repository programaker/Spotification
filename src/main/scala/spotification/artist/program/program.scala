package spotification.artist

import spotification.artist.service.{GetMyFollowedArtistsServiceR, getMyFollowedArtists}
import spotification.common.program.{PageRIO, paginate}
import spotification.effect.unitRIO
import zio.RIO

package object program {
  def paginateMyFollowedArtistsPar[R <: GetMyFollowedArtistsServiceR](req: GetMyFollowedArtistsRequest[_])(
    f: List[ArtistId] => RIO[R, Unit]
  ): RIO[R, Unit] =
    paginate(unitRIO[R])(fetchMyFollowedArtistsPage[R])((rio, artistIds) => rio &> f(artistIds))(req)

  def fetchMyFollowedArtistsPage[R <: GetMyFollowedArtistsServiceR](
    req: GetMyFollowedArtistsRequest[_]
  ): PageRIO[R, ArtistId, GetMyFollowedArtistsRequest[_]] =
    getMyFollowedArtists(req).map(getMyFollowedArtistsPage(req, _))
}
