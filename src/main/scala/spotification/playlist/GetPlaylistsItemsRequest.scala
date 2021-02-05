package spotification.playlist

import spotification.authorization.AccessToken
import spotification.common.{PositiveInt, UriString}
import spotification.playlist.GetPlaylistsItemsRequest.RequestType
import spotification.playlist.GetPlaylistsItemsRequest.RequestType.{First, Next}

final case class GetPlaylistsItemsRequest[T <: RequestType](accessToken: AccessToken, requestType: T)
object GetPlaylistsItemsRequest {
  def first(
    accessToken: AccessToken,
    playlistId: PlaylistId,
    limit: PositiveInt
  ): GetPlaylistsItemsRequest[First] =
    GetPlaylistsItemsRequest(accessToken, First(playlistId, limit))

  def next(accessToken: AccessToken, nextUri: UriString): GetPlaylistsItemsRequest[Next] =
    GetPlaylistsItemsRequest(accessToken, Next(nextUri))

  def next(req: GetPlaylistsItemsRequest[_], nextUri: UriString): GetPlaylistsItemsRequest[Next] =
    next(req.accessToken, nextUri)

  sealed trait RequestType
  object RequestType {
    final case class First(
      playlistId: PlaylistId,
      limit: PositiveInt
    ) extends RequestType

    final case class Next(
      nextUri: UriString
    ) extends RequestType
  }
}
