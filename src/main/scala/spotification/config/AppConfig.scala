package spotification.config

import spotification.spotify.authorization._
import spotification.spotify.playlist.PlaylistApiUri
import spotification.track.TrackApiUri
import spotification.common.{Host, PositiveInt}

import scala.concurrent.duration.FiniteDuration

final case class AppConfig(
  authorization: AuthorizationConfig,
  playlist: PlaylistConfig,
  track: TrackConfig,
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

final case class TrackConfig(
  trackApiUri: TrackApiUri
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
