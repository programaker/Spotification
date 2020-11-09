package spotification

import cats.Show
import cats.implicits._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.cats._
import eu.timepit.refined.refineV
import eu.timepit.refined.string.MatchesRegex
import io.estatico.newtype.macros.newtype
import io.estatico.newtype.ops._
import spotification.common.{SpotifyId, SpotifyIdR, UriR, UriString}

package object track {
  type TrackUriR = MatchesRegex["^spotify:track:[0-9a-zA-Z]+$"]
  type TrackUri = String Refined TrackUriR

  @newtype case class TrackId(value: SpotifyId)
  object TrackId {
    implicit val TrackIdShow: Show[TrackId] = implicitly[Show[SpotifyId]].coerce

    def fromUri(uri: TrackUri): TrackId = {
      val s"spotify:track:$id" = uri.show

      // type `TrackUri` already ensures that `id` exists and is a valid SpotifyId
      TrackId(refineV[SpotifyIdR].unsafeFrom(id))
    }
  }

  @newtype case class TrackApiUri(value: UriString)
  object TrackApiUri {
    implicit val TrackApiUriShow: Show[TrackApiUri] = implicitly[Show[UriString]].coerce
  }

  def makeTrackUri(trackApiUri: TrackApiUri, trackId: TrackId): Either[String, UriString] =
    refineV[UriR](show"$trackApiUri/$trackId")

  def makeShareTrackString(resp: GetTrackResponse): String =
    show"🎶 '${resp.name}' by '${resp.artists.mkString_(", ")}' - ${resp.external_urls.spotify}"
}
