package spotification.album

import spotification.track.TrackId
import spotification.effect.accessServiceFunction
import zio.{Has, RIO, Task}

package object service {
  type GetAlbumSampleTrackService = GetAlbumSampleTrackRequest => Task[TrackId]
  type GetAlbumSampleTrackServiceR = Has[GetAlbumSampleTrackService]
  def getAlbumSampleTrack(req: GetAlbumSampleTrackRequest): RIO[GetAlbumSampleTrackServiceR, TrackId] =
    accessServiceFunction(req)
}
