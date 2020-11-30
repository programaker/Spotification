package spotification.playlist

import eu.timepit.refined.auto._
import spotification.authorization.AccessToken
import spotification.common.{NonNegativeInt, PositiveInt, UriString}

sealed abstract class GetPlaylistsItemsRequest extends Product with Serializable
object GetPlaylistsItemsRequest {
  final case class FirstRequest(
    accessToken: AccessToken,
    playlistId: PlaylistId,
    limit: PositiveInt,
    offset: NonNegativeInt
  ) extends GetPlaylistsItemsRequest
  object FirstRequest {
    def make(accessToken: AccessToken, playlistId: PlaylistId, limit: PositiveInt): FirstRequest =
      FirstRequest(accessToken, playlistId, limit, offset = 0)
  }

  final case class NextRequest(
    accessToken: AccessToken,
    nextUri: UriString
  ) extends GetPlaylistsItemsRequest
}
