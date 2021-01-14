package spotification.track

import zio.{Has, RIO, Task, ZIO}

package object service {
  type GetTrackService = GetTrackRequest => Task[GetTrackResponse]
  type GetTrackServiceR = Has[GetTrackService]

  def getTrack(req: GetTrackRequest): RIO[GetTrackServiceR, GetTrackResponse] = ZIO.accessM(_.get.apply(req))
}
