package spotification.track.api

import eu.timepit.refined.refineV
import spotification.track.{TrackUri, TrackUriR}

object TrackUriVar {
  def unapply(pathVar: String): Option[TrackUri] = refineV[TrackUriR](pathVar).toOption
}
