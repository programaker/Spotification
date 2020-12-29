package spotification

import cats.Show
import cats.syntax.eq._
import eu.timepit.refined.cats._
import eu.timepit.refined.auto._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean.Or
import eu.timepit.refined.generic.Equal
import eu.timepit.refined.string.MatchesRegex
import io.estatico.newtype.macros.newtype
import io.estatico.newtype.ops.toCoercibleIdOps
import spotification.common.SpotifyId

package object album {
  type AlbumTypeR = Equal["album"] Or Equal["single"] Or Equal["compilation"]
  type AlbumType = String Refined AlbumTypeR

  type ReleaseDateStringR = MatchesRegex["""^\d{4}(-\d{2})?(-\d{2})?$"""]
  type ReleaseDateString = String Refined ReleaseDatePrecisionR

  type ReleaseDatePrecisionR = Equal["year"] Or Equal["month"] Or Equal["day"]
  type ReleaseDatePrecision = String Refined ReleaseDatePrecisionR

  @newtype case class AlbumId(value: SpotifyId)
  object AlbumId {
    implicit val AlbumIdShow: Show[AlbumId] = implicitly[Show[SpotifyId]].coerce
  }

  def isAlbum(albumType: AlbumType): Boolean = albumType === "album"
}
