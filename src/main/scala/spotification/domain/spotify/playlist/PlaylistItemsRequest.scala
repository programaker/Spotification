package spotification.domain.spotify.playlist

import spotification.domain.spotify.authorization.AccessToken
import spotification.domain.{FieldsToReturn, NonNegativeInt, PositiveInt, UriString}

sealed abstract class PlaylistItemsRequest extends Product with Serializable
object PlaylistItemsRequest {
  final case class FirstRequest(
    accessToken: AccessToken,
    playlistId: PlaylistId,
    fields: FieldsToReturn,
    limit: PositiveInt,
    offset: NonNegativeInt
  ) extends PlaylistItemsRequest

  final case class NextRequest(
    accessToken: AccessToken,
    nextUri: UriString
  ) extends PlaylistItemsRequest
}
