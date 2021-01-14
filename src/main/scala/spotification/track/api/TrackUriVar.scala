package spotification.track.api

import eu.timepit.refined.refineV
import spotification.track.{TrackUri, TrackUriP}

object TrackUriVar {
  def unapply(pathVar: String): Option[TrackUri] = refineV[TrackUriP](pathVar).toOption
}
