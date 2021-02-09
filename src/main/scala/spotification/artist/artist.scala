package spotification

import cats.Show
import cats.syntax.eq._
import cats.syntax.show._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean.Or
import eu.timepit.refined.cats.{refTypeShow, _}
import eu.timepit.refined.generic.Equal
import eu.timepit.refined.numeric.Interval
import eu.timepit.refined.string.MatchesRegex
import io.estatico.newtype.macros.newtype
import spotification.album.ReleaseDatePrecision
import spotification.common._
import spotification.me.MeApiUri

package object artist {
  type IncludeAlbumGroupP =
    Equal["album"] Or
      Equal["single"] Or
      Equal["appears_on"] Or
      Equal["compilation"]
  type IncludeAlbumGroup = String Refined IncludeAlbumGroupP

  type IncludeAlbumGroupsStringP = MatchesRegex["""^[a-z]([a-z_])+(\,([a-z_])+)*[a-z]$"""]
  type IncludeAlbumGroupsString = String Refined IncludeAlbumGroupsStringP

  type MyFollowedArtistsToProcessMax = 50
  type MyFollowedArtistsLimitP = Interval.Closed[1, MyFollowedArtistsToProcessMax]
  type MyFollowedArtistsLimit = Int Refined MyFollowedArtistsLimitP
  object MyFollowedArtistsLimit {
    val MaxValue: MyFollowedArtistsLimit = refineU[MyFollowedArtistsLimitP](valueOf[MyFollowedArtistsToProcessMax])
  }

  type ArtistAlbumsToProcessMax = 50
  type ArtistAlbumsLimitP = Interval.Closed[1, ArtistAlbumsToProcessMax]
  type ArtistAlbumsLimit = Int Refined ArtistAlbumsLimitP
  object ArtistAlbumsLimit {
    val MaxValue: ArtistAlbumsLimit = refineU[ArtistAlbumsLimitP](valueOf[ArtistAlbumsToProcessMax])
  }

  @newtype case class ArtistId(value: SpotifyId)
  object ArtistId {
    implicit val ArtistIdShow: Show[ArtistId] = deriving
  }

  @newtype case class ArtistApiUri(value: UriString)
  object ArtistApiUri {
    implicit val ArtistApiUriShow: Show[ArtistApiUri] = deriving
  }

  def joinIncludeAlbumGroups(groups: List[IncludeAlbumGroup]): Either[RefinementError, IncludeAlbumGroupsString] =
    joinRefinedStrings(groups, ",")

  def addIncludeAlbumGroupsParam(params: ParamMap, groups: List[IncludeAlbumGroup]): Either[RefinementError, ParamMap] =
    joinIncludeAlbumGroups(groups).map(addRefinedStringParam("include_groups", params, _))

  def artistsAlbumsUri(artistApiUri: ArtistApiUri, artistId: ArtistId): Either[RefinementError, UriString] =
    refineE[UriStringP](show"$artistApiUri/$artistId/albums")

  def hasReleaseDatePrecision(album: GetArtistsAlbumsResponse.Album, precision: ReleaseDatePrecision): Boolean =
    album.release_date_precision === precision

  def hasReleaseDate(album: GetArtistsAlbumsResponse.Album, date: MonthDay): Boolean =
    refineE[YearMonthDayStringP](album.release_date.value)
      .map(MonthDay.fromYearMonthDayString)
      .contains(date)

  def makeMyFollowedArtistsUri(meApiUri: MeApiUri): Either[RefinementError, UriString] =
    refineE[UriStringP](show"$meApiUri/following")

  def getMyFollowedArtistsPage(
    req: GetMyFollowedArtistsRequest[_],
    resp: GetMyFollowedArtistsResponse
  ): Page[ArtistId, GetMyFollowedArtistsRequest[_]] =
    Page(resp.artistIds, resp.next.map(req.next))
}
