package spotification.domain.spotify

import cats.Show
import io.estatico.newtype.macros.newtype
import io.estatico.newtype.ops._
import spotification.domain.{SpotifyId, UriString}
import cats.implicits._
import eu.timepit.refined.cats._

package object user {
  @newtype case class UserId(value: SpotifyId)
  object UserId {
    implicit val userIdShow: Show[UserId] = implicitly[Show[SpotifyId]].coerce
  }

  @newtype case class UserApiUri(value: UriString)
  object UserApiUri {
    implicit val playlistApiUriShow: Show[UserApiUri] = implicitly[Show[UriString]].coerce
  }
}
