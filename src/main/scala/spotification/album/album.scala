package spotification

import cats.Show
import cats.syntax.show._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.boolean.Or
import eu.timepit.refined.cats._
import eu.timepit.refined.generic.Equal
import eu.timepit.refined.string.MatchesRegex
import io.estatico.newtype.macros.newtype
import spotification.common._

package object album {
  type AlbumTypeP = Equal["album"] Or Equal["single"] Or Equal["compilation"]
  type AlbumType = String Refined AlbumTypeP
  object AlbumType {
    val Album: AlbumType = "album"
  }

  type ReleaseDateStringP = MatchesRegex["""^\d{4}(-\d{2})?(-\d{2})?$"""]
  type ReleaseDateString = String Refined ReleaseDateStringP

  type ReleaseDatePrecisionP = Equal["year"] Or Equal["month"] Or Equal["day"]
  type ReleaseDatePrecision = String Refined ReleaseDatePrecisionP
  object ReleaseDatePrecision {
    val Day: ReleaseDatePrecision = "day"
  }

  type AlbumTrackSampleLimitValue = 1
  type AlbumTrackSampleLimitP = Equal[AlbumTrackSampleLimitValue]
  type AlbumTrackSampleLimit = Int Refined AlbumTrackSampleLimitP
  object AlbumTrackSampleLimit {
    val Value: AlbumTrackSampleLimit = refineU[AlbumTrackSampleLimitP](valueOf[AlbumTrackSampleLimitValue])
  }

  @newtype case class AlbumId(value: SpotifyId)
  object AlbumId {
    implicit val AlbumIdShow: Show[AlbumId] = deriving
  }

  @newtype case class AlbumApiUri(value: UriString)
  object AlbumApiUri {
    implicit val AlbumApiUriShow: Show[AlbumApiUri] = deriving
  }

  def albumsTracksUri(albumApiUri: AlbumApiUri, albumId: AlbumId): Either[RefinementError, UriString] =
    refineE[UriStringP](show"$albumApiUri/$albumId/tracks")
}
