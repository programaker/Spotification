package spotification

import cats.Show
import cats.implicits._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.cats._
import eu.timepit.refined.string.MatchesRegex
import io.estatico.newtype.macros.newtype
import spotification.common._

package object track {
  type TrackUriP = MatchesRegex["^spotify:track:[0-9a-zA-Z]+$"]
  type TrackUri = String Refined TrackUriP

  @newtype case class TrackId(value: SpotifyId)
  object TrackId {
    implicit val TrackIdShow: Show[TrackId] = deriving

    def fromUri(uri: TrackUri): TrackId = {
      val s"spotify:track:$id" = uri.show

      // type `TrackUri` already ensures that `id` exists and is a valid SpotifyId
      TrackId(refineU[SpotifyIdP](id))
    }
  }

  @newtype case class TrackApiUri(value: UriString)
  object TrackApiUri {
    implicit val TrackApiUriShow: Show[TrackApiUri] = deriving
  }

  def makeTrackUri(trackApiUri: TrackApiUri, trackId: TrackId): Either[RefinementError, UriString] =
    refineE[UriStringP](show"$trackApiUri/$trackId")

  def makeShareTrackString(resp: GetTrackResponse): String =
    show"🎶 '${resp.name}' by '${resp.artists.mkString_(", ")}' - ${resp.external_urls.spotify}"
}
