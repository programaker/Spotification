package spotification.spotify.authorization.application

import spotification.spotify.authorization.{
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
