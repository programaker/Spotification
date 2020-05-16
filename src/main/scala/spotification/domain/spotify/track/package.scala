package spotification.domain.spotify

import cats.Show
import cats.implicits._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.cats._
import eu.timepit.refined.string.MatchesRegex
import io.estatico.newtype.macros.newtype
import io.estatico.newtype.ops._
import spotification.domain.SpotifyId

package object track {
  @newtype case class TrackId(value: SpotifyId)
  object TrackId {
    implicit val trackIdShow: Show[TrackId] = implicitly[Show[SpotifyId]].coerce
  }

  type TrackUriR = MatchesRegex["^spotify:track:[0-9a-zA-Z]+$"]
  type TrackUri = String Refined TrackUriR
}
