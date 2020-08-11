package spotification.domain.spotify.track

import cats.implicits.showInterpolator
import eu.timepit.refined.refineV
import spotification.domain.{UriR, UriString}

object Track {
  def trackUri(trackApiUri: TrackApiUri, trackId: TrackId): Either[String, UriString] =
    refineV[UriR](show"$trackApiUri/$trackId")
}
