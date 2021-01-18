package spotification.track.api

import spotification.common.refineE
import spotification.track.{TrackUri, TrackUriP}

object TrackUriVar {
  def unapply(pathVar: String): Option[TrackUri] = refineE[TrackUriP](pathVar).toOption
}
