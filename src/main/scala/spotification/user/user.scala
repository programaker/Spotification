package spotification

import cats.Show
import eu.timepit.refined.cats.refTypeShow
import io.estatico.newtype.macros.newtype
import io.estatico.newtype.ops.toCoercibleIdOps
import spotification.common.{DayMonthString, SpotifyId, UriString, monthDayFromDayMonthString}

package object user {
  @newtype case class UserId(value: SpotifyId)
  object UserId {
    implicit val UserIdShow: Show[UserId] = implicitly[Show[SpotifyId]].coerce
  }

  @newtype case class UserApiUri(value: UriString)
  object UserApiUri {
    implicit val UserApiUriShow: Show[UserApiUri] = implicitly[Show[UriString]].coerce
  }

  def makeAnniversaryPlaylistInfo(dayMonth: DayMonthString): AnniversaryPlaylistInfo =
    AnniversaryPlaylistInfo.fromMonthDay(monthDayFromDayMonthString(dayMonth))
}
