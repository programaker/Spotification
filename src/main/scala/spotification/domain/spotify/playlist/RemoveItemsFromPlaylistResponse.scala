package spotification.domain.spotify.playlist

import spotification.domain.NonBlankString

sealed abstract class RemoveItemsFromPlaylistResponse extends Product with Serializable
object RemoveItemsFromPlaylistResponse {
  final case class Success(snapshot_id: NonBlankString) extends RemoveItemsFromPlaylistResponse
  final case class Error(status: Int, message: String) extends RemoveItemsFromPlaylistResponse
}
