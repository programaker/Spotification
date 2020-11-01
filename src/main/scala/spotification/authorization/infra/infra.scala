package spotification.authorization

import spotification.common.infra.httpclient.{H4sClient, HttpClientEnv}
import spotification.config.AuthorizationConfig
import spotification.config.application.AuthorizationConfigEnv
import spotification.authorization.application.spotifyauthorizarion.SpotifyAuthorizationEnv
import spotification.authorization.application.{AuthorizationService, AuthorizationServiceEnv}
import zio.{TaskLayer, ZLayer}

package object infra {
  val AuthorizationServiceLayer: TaskLayer[AuthorizationServiceEnv] = {
    val l1 = ZLayer.fromServices[AuthorizationConfig, H4sClient, AuthorizationService] { (config, httpClient) =>
      new H4sAuthorizationService(config.apiTokenUri, httpClient)
    }

    (AuthorizationConfigModule.live ++ HttpClientModule.live) >>> l1
  }

  val SpotifyAuthorizationLayer: TaskLayer[SpotifyAuthorizationEnv] =
    AuthorizationServiceLayer ++ AuthorizationConfigModule.live
}
