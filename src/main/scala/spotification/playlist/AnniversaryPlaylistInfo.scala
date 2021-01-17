package spotification.playlist

import cats.syntax.show._
import eu.timepit.refined._
import spotification.common.{MonthDay, NonBlankString, NonBlankStringP}

final case class AnniversaryPlaylistInfo(name: NonBlankString, description: Option[NonBlankString])
object AnniversaryPlaylistInfo {
  def fromMonthDay(md: MonthDay): AnniversaryPlaylistInfo = {
    val name = show"$md Album Anniversaries"
    val description = show"Songs from albums released on $md by the bands you follow"

    AnniversaryPlaylistInfo(
      refineV[NonBlankStringP].unsafeFrom(name),
      Some(refineV[NonBlankStringP].unsafeFrom(description))
    )
  }
}
