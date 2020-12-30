package spotification

import cats.Show
import cats.syntax.show._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.boolean.Or
import eu.timepit.refined.cats._
import eu.timepit.refined.generic.Equal
import eu.timepit.refined.refineV
import eu.timepit.refined.string.MatchesRegex
import io.estatico.newtype.macros.newtype
import spotification.common.{SpotifyId, UriString, UriStringR}

package object album {
  type AlbumTypeR = Equal["album"] Or Equal["single"] Or Equal["compilation"]
  type AlbumType = String Refined AlbumTypeR
  object AlbumType {
    val Album: AlbumType = "album"
  }

  type ReleaseDateStringR = MatchesRegex["""^\d{4}(-\d{2})?(-\d{2})?$"""]
  type ReleaseDateString = String Refined ReleaseDateStringR

  type ReleaseDatePrecisionR = Equal["year"] Or Equal["month"] Or Equal["day"]
  type ReleaseDatePrecision = String Refined ReleaseDatePrecisionR
  object ReleaseDatePrecision {
    val Day: ReleaseDatePrecision = "day"
  }

  type AlbumTrackSampleLimitValue = 1
  type AlbumTrackSampleLimitR = Equal[AlbumTrackSampleLimitValue]
  type AlbumTrackSampleLimit = Int Refined AlbumTrackSampleLimitR
  object AlbumTrackSampleLimit {
    val Value: AlbumTrackSampleLimit = refineV[AlbumTrackSampleLimitR].unsafeFrom(valueOf[AlbumTrackSampleLimitValue])
  }

  @newtype case class AlbumId(value: SpotifyId)
  object AlbumId {
    implicit val AlbumIdShow: Show[AlbumId] = deriving
  }

  @newtype case class AlbumApiUri(value: UriString)
  object AlbumApiUri {
    implicit val AlbumApiUriShow: Show[AlbumApiUri] = deriving
  }

  def albumsTracksUri(albumApiUri: AlbumApiUri, albumId: AlbumId): Either[String, UriString] =
    refineV[UriStringR](show"$albumApiUri/$albumId/tracks")
}
