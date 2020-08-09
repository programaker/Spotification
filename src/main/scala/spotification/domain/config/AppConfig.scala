package spotification.domain.config

import spotification.domain.spotify.authorization._
import spotification.domain.spotify.playlist.PlaylistApiUri
import spotification.domain.{Host, PositiveInt}

import scala.concurrent.duration.FiniteDuration

final case class AppConfig(
  authorization: AuthorizationConfig,
  playlist: PlaylistConfig,
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
