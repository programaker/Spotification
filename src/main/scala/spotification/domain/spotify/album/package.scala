package spotification.domain.spotify

import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean.Or
import eu.timepit.refined.generic.Equal
import eu.timepit.refined.auto._
import io.estatico.newtype.macros.newtype
import spotification.domain.SpotifyId
import cats.implicits._

package object album {
  @newtype case class AlbumId(value: SpotifyId)

  type AlbumTypeR = Equal["album"] Or Equal["single"] Or Equal["compilation"]
  type AlbumType = String Refined AlbumTypeR
  object AlbumType {
    val isAlbum: AlbumType => Boolean = "album" === _
  }
}
