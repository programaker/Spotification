package spotification

import cats.Show
import eu.timepit.refined.cats.refTypeShow
import eu.timepit.refined.auto._
import io.estatico.newtype.macros.newtype
import io.estatico.newtype.ops.toCoercibleIdOps
import spotification.common.{NonBlankString, SpotifyId, UriString}

import java.time.MonthDay
import scala.util.Try

package object user {
  @newtype case class UserId(value: SpotifyId)
  object UserId {
    implicit val UserIdShow: Show[UserId] = implicitly[Show[SpotifyId]].coerce
  }

  @newtype case class UserApiUri(value: UriString)
  object UserApiUri {
    implicit val UserApiUriShow: Show[UserApiUri] = implicitly[Show[UriString]].coerce
  }

  def makeAnniversaryPlaylistInfo(rawMonthDay: NonBlankString): Either[Throwable, AnniversaryPlaylistInfo] =
    Try(MonthDay.parse(rawMonthDay, AnniversaryPlaylistInfo.CreateFormatter)).toEither
      .map(AnniversaryPlaylistInfo.fromMonthDay)
}
