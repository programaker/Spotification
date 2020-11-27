package spotification.artist.service

import spotification.artist.{GetMyFollowedArtistsRequest, GetMyFollowedArtistsResponse}
import zio.Task

trait ArtistService {
  def getMyFollowedArtists(req: GetMyFollowedArtistsRequest): Task[GetMyFollowedArtistsResponse]
}
