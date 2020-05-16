package spotification.domain.config

import spotification.domain.spotify.album.AlbumApiUri
import spotification.domain.spotify.authorization._
import spotification.domain.spotify.playlist.{PlaylistApiUri, PlaylistId}
import spotification.domain.{Host, PositiveInt}

import scala.concurrent.duration.FiniteDuration

final case class AppConfig(
  authorization: AuthorizationConfig,
  playlist: PlaylistConfig,
  album: AlbumConfig,
  server: ServerConfig,
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
  playlistApiUri: PlaylistApiUri
)

final case class AlbumConfig(
  albumApiUri: AlbumApiUri
)

final case class ServerConfig(
  host: Host,
  port: PositiveInt
)

final case class LogConfig(
  logDir: Directory,
  maxFileSizeInBytes: Option[Bytes],
  rolloverInterval: Option[FiniteDuration]
)
