package spotification.domain.spotify.authorization

import spotification.domain._
import spotification.domain.config.SpotifyConfig

final case class AccessTokenRequest(
  client_id: ClientId,
  client_secret: ClientSecret,
  grant_type: AccessTokenGrantType,
  code: NonBlankString,
  redirect_uri: UriString
)
object AccessTokenRequest {
  def make(cfg: SpotifyConfig, code: NonBlankString): AccessTokenRequest = AccessTokenRequest(
    client_id = cfg.clientId,
    client_secret = cfg.clientSecret,
    grant_type = AccessTokenGrantType.AuthorizationCode,
    code = code,
    redirect_uri = cfg.redirectUri
  )
}
