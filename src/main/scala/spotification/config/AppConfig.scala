package spotification.config

import spotification.authorization._
import spotification.common.{Host, PositiveInt}
import spotification.playlist.PlaylistApiUri
import spotification.track.TrackApiUri
import spotification.user.MeApiUri

import scala.concurrent.duration.FiniteDuration

final case class AppConfig(
  authorization: AuthorizationConfig,
  playlist: PlaylistConfig,
  track: TrackConfig,
  user: UserConfig,
  server: ServerConfig,
  client: ClientConfig,
  concurrent: ConcurrentConfig
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

final case class UserConfig(
  meApiUri: MeApiUri
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

final case class ConcurrentConfig(
  numberOfThreads: PositiveInt
)
