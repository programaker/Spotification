package spotification

import cats.Show
import cats.syntax.eq._
import eu.timepit.refined.cats._
import eu.timepit.refined.auto._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean.Or
import eu.timepit.refined.generic.Equal
import io.estatico.newtype.macros.newtype
import io.estatico.newtype.ops.toCoercibleIdOps
import spotification.common.SpotifyId

package object album {
  type AlbumTypeR = Equal["album"] Or Equal["single"] Or Equal["compilation"]
  type AlbumType = String Refined AlbumTypeR

  type ReleaseDatePrecisionR = Equal["day"] Or Equal["month"] Or Equal["year"]
  type ReleaseDatePrecision = String Refined ReleaseDatePrecisionR

  @newtype case class AlbumId(value: SpotifyId)
  object AlbumId {
    implicit val AlbumIdShow: Show[AlbumId] = implicitly[Show[SpotifyId]].coerce
  }

  def isAlbum(albumType: AlbumType): Boolean = albumType === "album"
}
