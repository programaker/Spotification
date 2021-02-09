package spotification.track

import spotification.effect.accessServiceFunction
import zio.{Has, RIO, Task}

package object service {
  type GetTrackService = GetTrackRequest => Task[GetTrackResponse]
  type GetTrackServiceR = Has[GetTrackService]
  def getTrack(req: GetTrackRequest): RIO[GetTrackServiceR, GetTrackResponse] = accessServiceFunction(req)
}
