package spotification.domain.config

import spotification.domain.spotify.authorization._
import spotification.domain.spotify.playlist.{PlaylistApiUri, PlaylistId}
import spotification.domain.{Host, PositiveInt}

import scala.concurrent.duration.FiniteDuration

final case class AppConfig(
  authorization: AuthorizationConfig,
  playlist: PlaylistConfig,
  server: ServerConfig,
  client: ClientConfig,
  log: LogConfig
)

final case class AuthorizationConfig(
  clientId: ClientId,
  clientSecret: ClientSecret,
  redirectUri: RedirectUri,
  authorizeUri: AuthorizeUri,
  apiTokenUri: ApiTokenUri,
  scopes: Option[List[Scope]],
  refreshToken: Option[RefreshToken]
)

final case class PlaylistConfig(
  releaseRadarId: PlaylistId,
  releaseRadarNoSinglesId: PlaylistId,
  mergedPlaylistId: PlaylistId,
  playlistsToMerge: List[PlaylistId],
  playlistApiUri: PlaylistApiUri,
  getPlaylistItemsLimit: PositiveInt
)

final case class ServerConfig(
  host: Host,
  port: PositiveInt
)

final case class ClientConfig(
  logHeaders: Boolean,
  logBody: Boolean
)

final case class LogConfig(
  logDir: Directory,
  maxFileSizeInBytes: Option[Bytes],
  rolloverInterval: Option[FiniteDuration]
)
