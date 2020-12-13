package spotification

import cats.Show
import cats.syntax.show._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Interval
import eu.timepit.refined.refineV
import eu.timepit.refined.auto._
import eu.timepit.refined.cats.refTypeShow
import io.estatico.newtype.macros.newtype
import io.estatico.newtype.ops.toCoercibleIdOps
import spotification.common.{UriString, UriStringR}

package object me {
  @newtype case class MeApiUri(value: UriString)
  object MeApiUri {
    implicit val MeApiUriShow: Show[MeApiUri] = implicitly[Show[UriString]].coerce
  }

  type MyFollowedArtistsToProcessMax = 50
  type MyFollowedArtistsLimitR = Interval.Closed[1, MyFollowedArtistsToProcessMax]
  type MyFollowedArtistsLimit = Int Refined MyFollowedArtistsLimitR
  object MyFollowedArtistsLimit {
    val MaxValue: MyFollowedArtistsLimit = 50
  }

  def makeMyFollowedArtistsUri(meApiUri: MeApiUri): Either[String, UriString] =
    refineV[UriStringR](show"$meApiUri/following")
}
