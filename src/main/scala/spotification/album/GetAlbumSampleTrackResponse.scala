package spotification.album

import spotification.track.TrackId

final case class GetAlbumSampleTrackResponse(items: List[GetAlbumSampleTrackResponse.Track])
object GetAlbumSampleTrackResponse {
  final case class Track(id: TrackId)
}
