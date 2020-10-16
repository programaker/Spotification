package spotification.domain.spotify

import cats.implicits._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean.Or
import eu.timepit.refined.generic.Equal

package object album {
  type AlbumTypeR = Equal["album"] Or Equal["single"] Or Equal["compilation"]
  type AlbumType = String Refined AlbumTypeR

  def isAlbum(albumType: AlbumType): Boolean = albumType.value === "album"
}
