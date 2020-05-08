package spotification.presentation

import spotification.infra.BaseZIO
import spotification.infra.config.ConfigZIO
import spotification.infra.httpclient.HttpClientZIO.HttpClientService
import spotification.infra.spotify.authorization.AuthorizationZIO
import spotification.infra.spotify.authorization.AuthorizationZIO.AuthorizationEnv
import zio.RLayer

object PresentationZIO {
  type PresentationEnv = AuthorizationEnv //while we have only one controller

  val layer: RLayer[HttpClientService, PresentationEnv] =
    AuthorizationZIO.layer ++ ConfigZIO.spotifyConfigLayer ++ BaseZIO.layer
}
