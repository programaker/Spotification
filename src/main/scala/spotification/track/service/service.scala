package spotification.track

import zio.{Has, RIO, ZIO}

package object service {
  type TrackServiceEnv = Has[TrackService]

  def getTrack(req: GetTrackRequest): RIO[TrackServiceEnv, GetTrackResponse] =
    ZIO.accessM(_.get.getTrack(req))
}
