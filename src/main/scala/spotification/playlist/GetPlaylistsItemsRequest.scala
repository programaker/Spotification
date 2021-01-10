package spotification.playlist

import eu.timepit.refined.auto._
import spotification.authorization.AccessToken
import spotification.common.{NonNegativeInt, PositiveInt, UriString}
import spotification.playlist.GetPlaylistsItemsRequest.RequestType
import spotification.playlist.GetPlaylistsItemsRequest.RequestType.{First, Next}

final case class GetPlaylistsItemsRequest[T <: RequestType](accessToken: AccessToken, requestType: T)
object GetPlaylistsItemsRequest {
  def first(
    accessToken: AccessToken,
    playlistId: PlaylistId,
    limit: PositiveInt
  ): GetPlaylistsItemsRequest[First] =
    GetPlaylistsItemsRequest(accessToken, First(playlistId, limit, offset = 0))

  def next(accessToken: AccessToken, nextUri: UriString): GetPlaylistsItemsRequest[Next] =
    GetPlaylistsItemsRequest(accessToken, Next(nextUri))

  sealed trait RequestType
  object RequestType {
    final case class First(
      playlistId: PlaylistId,
      limit: PositiveInt,
      offset: NonNegativeInt
    ) extends RequestType

    final case class Next(
      nextUri: UriString
    ) extends RequestType
  }
}
