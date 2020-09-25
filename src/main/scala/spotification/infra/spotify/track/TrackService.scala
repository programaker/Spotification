package spotification.infra.spotify.track

import spotification.domain.spotify.track.{GetTrackRequest, GetTrackResponse}
import zio.Task

trait TrackService {
  def getTrack(req: GetTrackRequest): Task[GetTrackResponse]
}
