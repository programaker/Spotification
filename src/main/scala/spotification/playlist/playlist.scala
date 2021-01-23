package spotification

import cats.Show
import cats.syntax.eq._
import cats.syntax.show._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean.And
import eu.timepit.refined.cats._
import eu.timepit.refined.collection.{MaxSize, MinSize}
import io.estatico.newtype.macros.newtype
import spotification.album.AlbumType
import spotification.common.{DayMonthString, MonthDay, RefinementError, SpotifyId, UriString, UriStringP, refineE}
import spotification.playlist.GetPlaylistsItemsResponse.TrackResponse
import spotification.track.TrackUri
import spotification.user.{UserApiUri, UserId}

package object playlist {
  // A maximum of 100 Tracks can be processed in a single request
  // An IndexedSeq is being used due to efficient `length` operation (needed for the refinement)
  type PlaylistItemsToProcessMax = 100
  type PlaylistItemsToProcessP = MinSize[1] And MaxSize[PlaylistItemsToProcessMax]
  type PlaylistItemsToProcess[A] = Vector[A] Refined PlaylistItemsToProcessP
  object PlaylistItemsToProcess {
    val MaxSize: PlaylistItemsToProcessMax = valueOf[PlaylistItemsToProcessMax]
  }

  @newtype case class PlaylistId(value: SpotifyId)
  object PlaylistId {
    implicit val PlaylistIdShow: Show[PlaylistId] = deriving
  }

  @newtype case class PlaylistApiUri(value: UriString)
  object PlaylistApiUri {
    implicit val PlaylistApiUriShow: Show[PlaylistApiUri] = deriving
  }

  def playlistTracksUri(playlistApiUri: PlaylistApiUri, playlistId: PlaylistId): Either[RefinementError, UriString] =
    refineE[UriStringP](show"$playlistApiUri/$playlistId/tracks")

  def userPlaylistsUri(userApiUri: UserApiUri, userId: UserId): Either[RefinementError, UriString] =
    refineE[UriStringP](show"$userApiUri/$userId/playlists")

  def trackUriIfAlbum(track: TrackResponse): Option[TrackUri] =
    if (track.album.album_type === AlbumType.Album) Some(track.uri)
    else None

  def makeAnniversaryPlaylistInfo(dayMonth: DayMonthString): AnniversaryPlaylistInfo =
    AnniversaryPlaylistInfo.fromMonthDay(MonthDay.fromDayMonthString(dayMonth))
}
