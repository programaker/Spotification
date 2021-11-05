package spotification.config

import spotification.album.AlbumApiUri
import spotification.artist.ArtistApiUri
import spotification.authorization._
import spotification.common.{Host, PositiveInt}
import spotification.me.MeApiUri
import spotification.playlist.PlaylistApiUri
import spotification.track.TrackApiUri
import spotification.user.UserApiUri

import scala.concurrent.duration.FiniteDuration

final case class AppConfig(
  authorization: AuthorizationConfig,
  playlist: PlaylistConfig,
  artist: ArtistConfig,
  album: AlbumConfig,
  track: TrackConfig,
  me: MeConfig,
  user: UserConfig,
  server: ServerConfig,
  client: ClientConfig
)

final case class AuthorizationConfig(
  clientId: ClientId,
  clientSecret: ClientSecret,
  redirectUri: RedirectUri,
  authorizeUri: AuthorizeUri,
  apiTokenUri: ApiTokenUri,
  scopes: Option[List[Scope]]
)

final case class PlaylistConfig(
  playlistApiUri: PlaylistApiUri,
  getPlaylistItemsLimit: PositiveInt,
  mergePlaylistsRetry: RetryConfig
)

final case class ArtistConfig(
  artistApiUri: ArtistApiUri
)

final case class AlbumConfig(
  albumApiUri: AlbumApiUri
)

final case class TrackConfig(
  trackApiUri: TrackApiUri
)

final case class MeConfig(
  meApiUri: MeApiUri
)

final case class UserConfig(
  userApiUri: UserApiUri
)

final case class ServerConfig(
  host: Host,
  port: PositiveInt
)

final case class ClientConfig(
  logHeaders: Boolean,
  logBody: Boolean
)

final case class RetryConfig(
  retryAfter: FiniteDuration,
  attempts: PositiveInt
)
