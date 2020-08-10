package spotification.domain.spotify.track

import spotification.domain.NonBlankString
import spotification.domain.spotify.track.GetTrackResponse.Success.ArtistResponse

sealed abstract class GetTrackResponse extends Product with Serializable
object GetTrackResponse {
  final case class Success(
    artists: List[ArtistResponse],
    name: NonBlankString
  ) extends GetTrackResponse
  object Success {
    final case class ArtistResponse(name: NonBlankString)
  }

  final case class Error(status: Int, message: String) extends GetTrackResponse
}
