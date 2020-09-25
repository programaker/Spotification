package spotification.infra.spotify.authorization

import spotification.domain.spotify.authorization.{
  AccessTokenRequest,
  AccessTokenResponse,
  RefreshTokenRequest,
  RefreshTokenResponse
}
import zio.Task

trait AuthorizationService {
  def requestToken(req: AccessTokenRequest): Task[AccessTokenResponse]
  def refreshToken(req: RefreshTokenRequest): Task[RefreshTokenResponse]
}
