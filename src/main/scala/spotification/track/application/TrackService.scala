package spotification.track.application

import spotification.track.{GetTrackRequest, GetTrackResponse}
import zio.Task

trait TrackService {
  def getTrack(req: GetTrackRequest): Task[GetTrackResponse]
}
