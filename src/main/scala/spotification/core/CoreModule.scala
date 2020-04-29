package spotification.core

import spotification.core.config.ConfigModule
import spotification.core.spotify.authorization.AuthorizationModule
import spotification.infra.httpclient.HttpClientModule.HttpClientService
import zio.ZLayer

object CoreModule {
  val layer: ZLayer[HttpClientService, Throwable, CoreServices] =
    ConfigModule.layer ++ AuthorizationModule.layer
}
