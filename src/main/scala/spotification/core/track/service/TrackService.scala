package spotification.core.track.service

import spotification.core.track.{GetTrackRequest, GetTrackResponse}
import zio.Task

trait TrackService {
  def getTrack(req: GetTrackRequest): Task[GetTrackResponse]
}
