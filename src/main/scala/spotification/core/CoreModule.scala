package spotification.core

import spotification.core.config.ConfigModule
import spotification.core.config.ConfigModule.ConfigServices
import spotification.core.spotify.authorization.AuthorizationModule
import spotification.core.spotify.authorization.AuthorizationModule.AuthorizationService
import spotification.infra.httpclient.HttpClientModule.HttpClientService
import zio.ZLayer

object CoreModule {
  // Provides the minimum environment for all ZIO functions
  // This way, we can give app-wide access to features such as
  // Console and Logging without break type signatures
  //
  // This means, never return Task[A] from ZIO functions; the minimum
  // function result should be `RIO[BaseEnv, A]` or `URIO[BaseEnv, A]`
  type BaseEnv = zio.ZEnv //TODO => add logging

  type CoreServices = ConfigServices with AuthorizationService

  val layer: ZLayer[HttpClientService, Throwable, CoreServices] =
    ConfigModule.layer ++ AuthorizationModule.layer
}
