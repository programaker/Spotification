package spotification.album

import spotification.track.TrackId
import zio.{Has, RIO, Task, ZIO}

package object service {
  type GetAlbumSampleTrackService = GetAlbumSampleTrackRequest => Task[TrackId]
  type GetAlbumSampleTrackServiceR = Has[GetAlbumSampleTrackService]

  def getAlbumSampleTrack(req: GetAlbumSampleTrackRequest): RIO[GetAlbumSampleTrackServiceR, TrackId] =
    ZIO.accessM(_.get.apply(req))
}
