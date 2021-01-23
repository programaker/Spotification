package spotification.playlist

import cats.syntax.show._
import spotification.common.{MonthDay, NonBlankString, NonBlankStringP, refineU}

final case class AnniversaryPlaylistInfo(name: NonBlankString, description: Option[NonBlankString])
object AnniversaryPlaylistInfo {
  def fromMonthDay(md: MonthDay): AnniversaryPlaylistInfo = {
    val name = show"$md Album Anniversaries"
    val description = show"Songs from albums released on $md by the bands you follow"
    AnniversaryPlaylistInfo(refineU[NonBlankStringP](name), Some(refineU[NonBlankStringP](description)))
  }
}
