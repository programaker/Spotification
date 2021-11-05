package spotification.album

import eu.timepit.refined.auto._
import spotification.authorization.AccessToken
import spotification.common.PositiveInt

final case class GetAlbumSampleTrackRequest(
  accessToken: AccessToken,
  albumId: AlbumId,
  limit: AlbumTrackSampleLimit,
  offset: PositiveInt
)
object GetAlbumSampleTrackRequest {

  /**
   * Creates a request to get a single Track from the Album, avoiding the first and the last (possible intro and outro)
   */
  def make(accessToken: AccessToken, albumId: AlbumId): GetAlbumSampleTrackRequest =
    GetAlbumSampleTrackRequest(
      accessToken,
      albumId,
      limit = AlbumTrackSampleLimit.Value,
      offset = 1
    )
}
