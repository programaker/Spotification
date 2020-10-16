package spotification.domain.spotify

import cats.Show
import cats.implicits._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean.And
import eu.timepit.refined.cats._
import eu.timepit.refined.collection.{MaxSize, MinSize}
import eu.timepit.refined.refineV
import io.estatico.newtype.macros.newtype
import io.estatico.newtype.ops._
import spotification.domain.spotify.album.isAlbum
import spotification.domain.spotify.authorization.AccessToken
import spotification.domain.spotify.playlist.GetPlaylistsItemsRequest.{FirstRequest, NextRequest}
import spotification.domain.spotify.playlist.GetPlaylistsItemsResponse.TrackResponse
import spotification.domain.spotify.track.TrackUri
import spotification.domain.{SpotifyId, UriR, UriString}

package object playlist {
  @newtype case class PlaylistId(value: SpotifyId)
  object PlaylistId {
    implicit val playlistIdShow: Show[PlaylistId] = implicitly[Show[SpotifyId]].coerce
  }

  @newtype case class PlaylistApiUri(value: UriString)
  object PlaylistApiUri {
    implicit val playlistApiUriShow: Show[PlaylistApiUri] = implicitly[Show[UriString]].coerce
  }

  // A maximum of 100 Tracks can be processed in a single request
  // An IndexedSeq is being used due to efficient `length` operation (needed for the refinement)
  type PlaylistItemsToProcessMax = 100
  type PlaylistItemsToProcessR = MinSize[1] And MaxSize[PlaylistItemsToProcessMax]
  type PlaylistItemsToProcess[A] = Vector[A] Refined PlaylistItemsToProcessR
  object PlaylistItemsToProcess {
    val maxSize: PlaylistItemsToProcessMax = 100
  }

  def playlistTracksUri(playlistApiUri: PlaylistApiUri, playlistId: PlaylistId): Either[String, UriString] =
    refineV[UriR](show"$playlistApiUri/$playlistId/tracks")

  def trackUriIfAlbum(track: TrackResponse): Option[TrackUri] =
    if (isAlbum(track.album.album_type)) Some(track.uri)
    else None

  def accessTokenFromRequest(req: GetPlaylistsItemsRequest): AccessToken =
    req match {
      case fr: FirstRequest => fr.accessToken
      case nr: NextRequest  => nr.accessToken
    }
}