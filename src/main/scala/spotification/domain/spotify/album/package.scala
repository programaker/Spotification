package spotification.domain.spotify

import cats.Show
import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean.{And, Or}
import eu.timepit.refined.generic.Equal
import eu.timepit.refined.collection.{MaxSize, MinSize}
import eu.timepit.refined.cats._
import io.estatico.newtype.macros.newtype
import io.estatico.newtype.ops._
import spotification.domain.{SpotifyId, UriString}
import cats.implicits._

package object album {
  @newtype case class AlbumId(value: SpotifyId)
  object AlbumId {
    implicit val AlbumIdShow: Show[AlbumId] = implicitly[Show[SpotifyId]].coerce
  }

  type AlbumTypeR = Equal["album"] Or Equal["single"] Or Equal["compilation"]
  type AlbumType = String Refined AlbumTypeR
  object AlbumType {
    val isAlbum: AlbumType => Boolean = _.value === "album"
  }

  // A maximum of 20 albums can be gotten at a time
  // An IndexedSeq is being used due to efficient `length` operation (needed for the refinement)
  type AlbumIdsToGetMax = 20
  type AlbumIdsToGetR = MinSize[1] And MaxSize[AlbumIdsToGetMax]
  type AlbumIdsToGet = Vector[AlbumId] Refined AlbumIdsToGetR
  object AlbumIdsToGet {
    val MaxSize: AlbumIdsToGetMax = 20
  }

  @newtype case class AlbumApiUri(value: UriString)
  object AlbumApiUri {
    implicit val AlbumApiUriShow: Show[AlbumApiUri] = implicitly[Show[UriString]].coerce
  }
}
