package spotification.spotify.track.application

import spotification.spotify.track.{GetTrackRequest, GetTrackResponse}
import zio.Task

trait TrackService {
  def getTrack(req: GetTrackRequest): Task[GetTrackResponse]
}
