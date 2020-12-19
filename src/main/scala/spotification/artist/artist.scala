package spotification

import cats.Show
import eu.timepit.refined.cats.refTypeShow
import io.estatico.newtype.macros.newtype
import io.estatico.newtype.ops.toCoercibleIdOps
import spotification.common.SpotifyId

package object artist {
  @newtype case class ArtistId(value: SpotifyId)
  object ArtistId {
    implicit val ArtistIdShow: Show[ArtistId] = implicitly[Show[SpotifyId]].coerce
  }
}
