package spotification.domain.spotify

import cats.Show
import cats.implicits._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.cats._
import eu.timepit.refined.refineV
import eu.timepit.refined.string.MatchesRegex
import io.estatico.newtype.macros.newtype
import io.estatico.newtype.ops._
import spotification.domain._

package object track {
  @newtype case class TrackId(value: SpotifyId)
  object TrackId {
    implicit val TrackIdShow: Show[TrackId] = implicitly[Show[SpotifyId]].coerce
  }

  @newtype case class TrackApiUri(value: UriString)
  object TrackApiUri {
    implicit val TrackApiUriShow: Show[TrackApiUri] = implicitly[Show[UriString]].coerce
  }

  type TrackUriR = MatchesRegex["^spotify:track:[0-9a-zA-Z]+$"]
  type TrackUri = String Refined TrackUriR

  def trackUri(trackApiUri: TrackApiUri, trackId: TrackId): Either[String, UriString] =
    refineV[UriR](show"$trackApiUri/$trackId")

  def trackIdFromUri(uri: TrackUri): TrackId = {
    val s"spotify:track:$id" = uri.show

    // Input type `TrackUri` already ensures that `id` is a valid SpotifyId
    TrackId(refineV[SpotifyIdR].unsafeFrom(id))
  }

  def makeShareTrackString(resp: GetTrackResponse): String =
    show"${resp.name} by ${resp.artists.mkString(", ")} - ${resp.external_urls.spotify}"
}
