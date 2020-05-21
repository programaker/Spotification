package spotification.domain.spotify.playlist

import spotification.domain.spotify.authorization.AccessToken
import spotification.domain.{FieldsToReturn, NonNegativeInt, PositiveInt, UriString}

sealed abstract class GetPlaylistsItemsRequest extends Product with Serializable {
  def accessToken: AccessToken
}
object GetPlaylistsItemsRequest {
  final case class FirstRequest(
    accessToken: AccessToken,
    playlistId: PlaylistId,
    fields: FieldsToReturn,
    limit: PositiveInt,
    offset: NonNegativeInt
  ) extends GetPlaylistsItemsRequest

  final case class NextRequest(
    accessToken: AccessToken,
    nextUri: UriString
  ) extends GetPlaylistsItemsRequest
}
