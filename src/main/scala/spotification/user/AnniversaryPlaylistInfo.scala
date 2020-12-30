package spotification.user

import cats.syntax.show._
import eu.timepit.refined._
import eu.timepit.refined.auto._
import spotification.common.{NonBlankString, NonBlankStringR}
import spotification.common.DayMonthString.DayMonthShow

import java.time.MonthDay

final case class AnniversaryPlaylistInfo(name: NonBlankString, description: Option[NonBlankString])
object AnniversaryPlaylistInfo {
  def fromMonthDay(md: MonthDay): AnniversaryPlaylistInfo =
    AnniversaryPlaylistInfo(
      name = refineV[NonBlankStringR].unsafeFrom(show"$md Album Anniversaries"),
      description = Some("Songs from albums released on that date by the bands I follow")
    )
}
