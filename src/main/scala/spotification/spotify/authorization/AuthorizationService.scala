package spotification.spotify.authorization

import zio.ZIO

trait AuthorizationService {
  def authorize(clientId: ClientId, scopes: List[Scope]): ZIO[Any, Nothing, Nothing] = ???

}
