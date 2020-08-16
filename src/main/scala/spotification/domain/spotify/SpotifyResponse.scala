package spotification.domain.spotify

import eu.timepit.refined.auto._
import spotification.domain.spotify.album.AlbumType
import spotification.domain.spotify.track.TrackUri
import spotification.domain._

sealed abstract class SpotifyResponse extends Product with Serializable
object SpotifyResponse {

  /** Generic `ErrorObject` used by most Spotify endpoints when something goes wrong */
  final case class Error(
    status: Int,
    message: String
  ) extends SpotifyResponse
}

object AuthorizationResponse {
  import spotification.domain.spotify.authorization.{AccessToken, RefreshToken, ScopeString, TokenType}

  final case class AuthorizeErrorResponse(
    error: String
  ) extends SpotifyResponse

  final case class AccessTokenResponse(
    access_token: AccessToken,
    refresh_token: RefreshToken,
    expires_in: PositiveInt,
    scope: Option[ScopeString],
    token_type: TokenType
  ) extends SpotifyResponse

  final case class RefreshTokenResponse(
    access_token: AccessToken,
    token_type: TokenType,
    expires_in: PositiveInt,
    scope: Option[String]
  ) extends SpotifyResponse
}

object PlaylistResponse {
  import spotification.domain.spotify.PlaylistResponse.GetPlaylistsItemsResponse.ItemResponse

  final case class PlaylistSnapshotResponse(
    snapshot_id: NonBlankString
  ) extends SpotifyResponse

  final case class GetPlaylistsItemsResponse(
    items: List[ItemResponse],
    href: CurrentUri,
    next: Option[NextUri]
  ) extends SpotifyResponse
  object GetPlaylistsItemsResponse {
    val Fields: FieldsToReturn = "items.track(uri,album(album_type)),href,next"
    final case class ItemResponse(track: Option[TrackResponse]) //`"track": null` = track unavailable
    final case class TrackResponse(album: AlbumResponse, uri: TrackUri)
    final case class AlbumResponse(album_type: AlbumType)
  }
}

object TrackResponse {
  import spotification.domain.spotify.TrackResponse.GetTrackResponse.ArtistResponse

  final case class GetTrackResponse(
    artists: List[ArtistResponse],
    name: NonBlankString
  ) extends SpotifyResponse
  object GetTrackResponse {
    final case class ArtistResponse(name: NonBlankString)
  }
}
