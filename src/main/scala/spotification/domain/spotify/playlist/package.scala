package spotification.domain.spotify

import cats.Show
import cats.implicits._
import io.estatico.newtype.macros.newtype
import io.estatico.newtype.ops._
import eu.timepit.refined.cats._
import spotification.domain.{SpotifyId, UriString}

package object playlist {
  @newtype case class PlaylistId(value: SpotifyId)
  object PlaylistId {
    implicit val playlistIdShow: Show[PlaylistId] = implicitly[Show[SpotifyId]].coerce
  }

  @newtype case class PlaylistApiUri(value: UriString)
  object PlaylistApiUri {
    implicit val playlistApiUriShow: Show[PlaylistApiUri] = implicitly[Show[UriString]].coerce
  }
}
