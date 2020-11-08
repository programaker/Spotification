package spotification.core.authorization

import spotification.core.NonBlankString
import spotification.core.config.AuthorizationConfig

final case class AccessTokenRequest(
  client_id: ClientId,
  client_secret: ClientSecret,
  grant_type: AccessTokenGrantType,
  code: NonBlankString,
  redirect_uri: RedirectUri
)
object AccessTokenRequest {
  def make(cfg: AuthorizationConfig, code: NonBlankString): AccessTokenRequest =
    AccessTokenRequest(
      cfg.clientId,
      cfg.clientSecret,
      AccessTokenGrantType.AuthorizationCode,
      code,
      cfg.redirectUri
    )
}
