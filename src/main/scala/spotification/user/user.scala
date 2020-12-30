package spotification

import cats.Show
import eu.timepit.refined.cats.refTypeShow
import io.estatico.newtype.macros.newtype
import spotification.common.{DayMonthString, MonthDay, SpotifyId, UriString}

package object user {
  @newtype case class UserId(value: SpotifyId)
  object UserId {
    implicit val UserIdShow: Show[UserId] = deriving
  }

  @newtype case class UserApiUri(value: UriString)
  object UserApiUri {
    implicit val UserApiUriShow: Show[UserApiUri] = deriving
  }

  def makeAnniversaryPlaylistInfo(dayMonth: DayMonthString): AnniversaryPlaylistInfo =
    AnniversaryPlaylistInfo.fromMonthDay(MonthDay.fromDayMonthString(dayMonth))
}
