package spotification.playlist

import eu.timepit.refined.auto._
import spotification.authorization.AccessToken
import spotification.common.{NonNegativeInt, PositiveInt, UriString}

sealed abstract class GetPlaylistsItemsRequest extends Product with Serializable
object GetPlaylistsItemsRequest {
  final case class FirstRequest(
    playlistId: PlaylistId,
    limit: PositiveInt,
    offset: NonNegativeInt,
    accessToken: AccessToken
  ) extends GetPlaylistsItemsRequest
  object FirstRequest {
    def make(playlistId: PlaylistId, limit: PositiveInt, accessToken: AccessToken): FirstRequest =
      FirstRequest(playlistId, limit, offset = 0, accessToken)
  }

  final case class NextRequest(
    nextUri: UriString,
    accessToken: AccessToken
  ) extends GetPlaylistsItemsRequest
}
