package spotification

import java.time.MonthDay

import cats.Show
import cats.implicits._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.boolean.And
import eu.timepit.refined.cats.refTypeShow
import eu.timepit.refined.collection.{MaxSize, MinSize}
import eu.timepit.refined.refineV
import io.estatico.newtype.macros.newtype
import io.estatico.newtype.ops._
import spotification.album.isAlbum
import spotification.common.{NonBlankString, SpotifyId, UriString, UriStringR}
import spotification.playlist.GetPlaylistsItemsResponse.TrackResponse
import spotification.track.TrackUri
import spotification.user.{UserApiUri, UserId}

import scala.util.Try

package object playlist {
  @newtype case class PlaylistId(value: SpotifyId)
  object PlaylistId {
    implicit val PlaylistIdShow: Show[PlaylistId] = implicitly[Show[SpotifyId]].coerce
  }

  @newtype case class PlaylistApiUri(value: UriString)
  object PlaylistApiUri {
    implicit val PlaylistApiUriShow: Show[PlaylistApiUri] = implicitly[Show[UriString]].coerce
  }

  // A maximum of 100 Tracks can be processed in a single request
  // An IndexedSeq is being used due to efficient `length` operation (needed for the refinement)
  type PlaylistItemsToProcessMax = 100
  type PlaylistItemsToProcessR = MinSize[1] And MaxSize[PlaylistItemsToProcessMax]
  type PlaylistItemsToProcess[A] = Vector[A] Refined PlaylistItemsToProcessR
  object PlaylistItemsToProcess {
    val MaxSize: PlaylistItemsToProcessMax = 100
  }

  def playlistTracksUri(playlistApiUri: PlaylistApiUri, playlistId: PlaylistId): Either[String, UriString] =
    refineV[UriStringR](show"$playlistApiUri/$playlistId/tracks")

  def userPlaylistsUri(userApiUri: UserApiUri, userId: UserId): Either[String, UriString] =
    refineV[UriStringR](show"$userApiUri/$userId/playlists")

  def trackUriIfAlbum(track: TrackResponse): Option[TrackUri] =
    if (isAlbum(track.album.album_type)) Some(track.uri)
    else None

  def makeAnniversaryPlaylistInfo(rawMonthDay: NonBlankString): Either[Throwable, AnniversaryPlaylistInfo] =
    Try(MonthDay.parse(rawMonthDay, AnniversaryPlaylistInfo.CreateFormatter)).toEither
      .map(AnniversaryPlaylistInfo.fromMonthDay)
}
