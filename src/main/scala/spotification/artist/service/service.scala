package spotification.artist

import zio.{Has, RIO, ZIO}

package object service {
  type ArtistServiceEnv = Has[ArtistService]

  def getMyFollowedArtists(req: GetMyFollowedArtistsRequest): RIO[ArtistServiceEnv, GetMyFollowedArtistsResponse] =
    ZIO.accessM(_.get.getMyFollowedArtists(req))
}
