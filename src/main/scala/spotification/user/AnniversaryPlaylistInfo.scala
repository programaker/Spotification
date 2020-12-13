package spotification.user

import eu.timepit.refined._
import eu.timepit.refined.auto._
import spotification.common.{NonBlankString, NonBlankStringR}

import java.time.MonthDay
import java.time.format.DateTimeFormatter

final case class AnniversaryPlaylistInfo(name: NonBlankString, description: Option[NonBlankString])
object AnniversaryPlaylistInfo {
  val CreateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM")
  val DisplayFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM")

  def fromMonthDay(md: MonthDay): AnniversaryPlaylistInfo =
    AnniversaryPlaylistInfo(
      name = refineV[NonBlankStringR].unsafeFrom(s"${md.format(DisplayFormatter)} Album Anniversaries"),
      description = Some("Songs from albums released on that date by the bands I follow")
    )
}
