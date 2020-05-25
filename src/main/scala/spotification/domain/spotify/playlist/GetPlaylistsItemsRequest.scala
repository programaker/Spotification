package spotification.domain.spotify.playlist

import spotification.domain.spotify.authorization.AccessToken
import spotification.domain.{NonNegativeInt, PositiveInt, UriString}

sealed abstract class GetPlaylistsItemsRequest extends Product with Serializable
object GetPlaylistsItemsRequest {
  final case class FirstRequest(
    accessToken: AccessToken,
    playlistId: PlaylistId,
    limit: PositiveInt,
    offset: NonNegativeInt
  ) extends GetPlaylistsItemsRequest

  final case class NextRequest(
    accessToken: AccessToken,
    nextUri: UriString
  ) extends GetPlaylistsItemsRequest

  def accessToken(req: GetPlaylistsItemsRequest): AccessToken =
    req match {
      case fr: FirstRequest => fr.accessToken
      case nr: NextRequest  => nr.accessToken
    }
}
