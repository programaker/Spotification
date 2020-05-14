package spotification.domain.spotify.playlist

import spotification.domain.NonBlankString

sealed abstract class AddItemsToPlaylistResponse extends Product with Serializable
object AddItemsToPlaylistResponse {
  final case class Success(snapshot_id: NonBlankString) extends AddItemsToPlaylistResponse
  final case class Error(status: Int, message: String) extends AddItemsToPlaylistResponse
}
