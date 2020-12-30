package spotification

import cats.Show
import cats.syntax.show._
import cats.syntax.eq._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.cats._
import eu.timepit.refined.auto._
import eu.timepit.refined.boolean.Or
import eu.timepit.refined.cats.refTypeShow
import eu.timepit.refined.generic.Equal
import eu.timepit.refined.numeric.Interval
import eu.timepit.refined.refineV
import eu.timepit.refined.string.MatchesRegex
import io.estatico.newtype.macros.newtype
import spotification.album.ReleaseDatePrecision
import spotification.common.{
  MonthDay,
  ParamMap,
  SpotifyId,
  UriString,
  UriStringR,
  YearMonthDayStringR,
  addRefinedStringParam,
  joinRefinedStrings
}

package object artist {
  type IncludeAlbumGroupR =
    Equal["album"] Or
      Equal["single"] Or
      Equal["appears_on"] Or
      Equal["compilation"]
  type IncludeAlbumGroup = String Refined IncludeAlbumGroupR

  type IncludeAlbumGroupsStringR = MatchesRegex["""^[a-z]([a-z_])+(\,([a-z_])+)*[a-z]$"""]
  type IncludeAlbumGroupsString = String Refined IncludeAlbumGroupsStringR

  type ArtistAlbumsToProcessMax = 50
  type ArtistAlbumsLimitR = Interval.Closed[1, ArtistAlbumsToProcessMax]
  type ArtistAlbumsLimit = Int Refined ArtistAlbumsLimitR
  object ArtistAlbumsLimit {
    val MaxValue: ArtistAlbumsLimit = refineV[ArtistAlbumsLimitR].unsafeFrom(valueOf[ArtistAlbumsToProcessMax])
  }

  @newtype case class ArtistId(value: SpotifyId)
  object ArtistId {
    implicit val ArtistIdShow: Show[ArtistId] = deriving
  }

  @newtype case class ArtistApiUri(value: UriString)
  object ArtistApiUri {
    implicit val ArtistApiUriShow: Show[ArtistApiUri] = deriving
  }

  def joinIncludeAlbumGroups(groups: List[IncludeAlbumGroup]): Either[String, IncludeAlbumGroupsString] =
    joinRefinedStrings(groups, ",")

  def addIncludeAlbumGroupsParam(params: ParamMap, groups: List[IncludeAlbumGroup]): Either[String, ParamMap] =
    joinIncludeAlbumGroups(groups).map(addRefinedStringParam("include_groups", params, _))

  def artistsAlbumsUri(artistApiUri: ArtistApiUri, artistId: ArtistId): Either[String, UriString] =
    refineV[UriStringR](show"$artistApiUri/$artistId/albums")

  def hasReleaseDatePrecision(album: GetArtistsAlbumsResponse.Album, precision: ReleaseDatePrecision): Boolean =
    album.release_date_precision === precision

  def hasReleaseDate(album: GetArtistsAlbumsResponse.Album, date: MonthDay): Boolean =
    refineV[YearMonthDayStringR](album.release_date.value)
      .map(MonthDay.fromYearMonthDayString)
      .contains(date)

}
