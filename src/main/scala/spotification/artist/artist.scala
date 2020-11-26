package spotification

import cats.Show
import eu.timepit.refined.api.Refined
import eu.timepit.refined.cats.refTypeShow
import eu.timepit.refined.numeric.Interval
import io.estatico.newtype.macros.newtype
import io.estatico.newtype.ops._
import spotification.common.SpotifyId

package object artist {
  @newtype case class ArtistId(value: SpotifyId)
  object ArtistId {
    implicit val ArtistIdShow: Show[ArtistId] = implicitly[Show[SpotifyId]].coerce
  }

  type FollowedArtistsToProcessMax = 50
  type FollowedArtistsLimitR = Interval.Closed[1, FollowedArtistsToProcessMax]
  type FollowedArtistsLimit = Int Refined FollowedArtistsLimitR
  object FollowedArtistsLimit {
    val MaxValue: FollowedArtistsToProcessMax = 50
  }
}
