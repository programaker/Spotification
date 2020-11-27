package spotification.artist.httpclient

import spotification.artist.{GetMyFollowedArtistsRequest, GetMyFollowedArtistsResponse}
import spotification.artist.service.ArtistService
import spotification.common.httpclient.H4sClient
import spotification.user.MeApiUri
import zio.Task

final class H4sArtistService(meApiUri: MeApiUri, httpClient: H4sClient) extends ArtistService {
  import H4sClient.Dsl._

  override def getMyFollowedArtists(req: GetMyFollowedArtistsRequest): Task[GetMyFollowedArtistsResponse] = ???
}
