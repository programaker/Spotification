package spotification.user

import cats.syntax.show._
import eu.timepit.refined._
import spotification.common.{MonthDay, NonBlankString, NonBlankStringR}

final case class AnniversaryPlaylistInfo(name: NonBlankString, description: Option[NonBlankString])
object AnniversaryPlaylistInfo {
  def fromMonthDay(md: MonthDay): AnniversaryPlaylistInfo = {
    val name = show"$md Album Anniversaries"
    val description = show"Songs from albums released on $md by the bands you follow"

    AnniversaryPlaylistInfo(
      refineV[NonBlankStringR].unsafeFrom(name),
      Some(refineV[NonBlankStringR].unsafeFrom(description))
    )
  }
}
