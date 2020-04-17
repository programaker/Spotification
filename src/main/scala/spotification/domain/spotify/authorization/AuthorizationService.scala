package spotification.domain.spotify.authorization

import zio.Task

trait AuthorizationService {
  def authorize(req: AuthorizeRequest): Task[Unit]
  def requestToken(req: AccessTokenRequest): Task[AccessTokenResponse]
  def refreshToken(req: RefreshTokenRequest): Task[RefreshTokenResponse]
}
