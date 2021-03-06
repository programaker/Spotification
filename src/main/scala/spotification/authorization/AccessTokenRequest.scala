package spotification.authorization

import spotification.common.NonBlankString
import spotification.config.AuthorizationConfig

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
