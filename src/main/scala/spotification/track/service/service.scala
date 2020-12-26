package spotification.track

import zio.{Has, RIO, Task, ZIO}

package object service {
  type GetTrackService = GetTrackRequest => Task[GetTrackResponse]
  type GetTrackServiceEnv = Has[GetTrackService]

  def getTrack(req: GetTrackRequest): RIO[GetTrackServiceEnv, GetTrackResponse] = ZIO.accessM(_.get.apply(req))
}
