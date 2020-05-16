package spotification.domain.spotify

import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean.{And, Or}
import eu.timepit.refined.generic.Equal
import io.estatico.newtype.macros.newtype
import spotification.domain.SpotifyId
import cats.implicits._
import eu.timepit.refined.collection.{MaxSize, MinSize}

package object album {
  @newtype case class AlbumId(value: SpotifyId)

  type AlbumTypeR = Equal["album"] Or Equal["single"] Or Equal["compilation"]
  type AlbumType = String Refined AlbumTypeR
  object AlbumType {
    val isAlbum: AlbumType => Boolean = _.value === "album"
  }

  // A maximum of 20 albums can be gotten at a time
  // An IndexedSeq is being used due to efficient `length` operation (needed for the refinement)
  type AlbumIdsToGetR = MinSize[1] And MaxSize[20]
  type AlbumIdsToGet = IndexedSeq[AlbumId] Refined AlbumIdsToGetR
}
