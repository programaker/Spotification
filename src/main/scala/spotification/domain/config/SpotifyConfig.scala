package spotification.domain.config

import spotification.domain.{NonBlankString, UriString}
import spotification.domain.spotify.authorization.{ClientId, ClientSecret, Scope}
import spotification.domain.spotify.playlist.PlaylistId

final case class SpotifyConfig(
  clientId: ClientId,
  clientSecret: ClientSecret,
  redirectUri: UriString,
  releaseRadarId: PlaylistId,
  scopes: Option[List[Scope]],
  refreshToken: Option[NonBlankString]
)
