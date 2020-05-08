package spotification

import spotification.infra.concurrent.ExecutionContextModule
import spotification.infra.config.ConfigServiceEnv
import spotification.infra.httpclient.HttpClientModule
import zio.{RLayer, ULayer, ZEnv}

package object infra {
  // Provides the minimum environment for all ZIO functions
  // This way, we can give app-wide access to features such as
  // Console and Logging without break type signatures
  //
  // This means, never return Task[A] from ZIO functions; the minimum
  // function result should be `RIO[BaseEnv, A]` or `URIO[BaseEnv, A]`
  type BaseEnv = ZEnv //TODO => add LogModule
  object BaseEnv {
    val layer: ULayer[ZEnv] = ZEnv.live
  }

  type ConfigServiceAndHttpClientEnv = ConfigServiceEnv with HttpClientModule
  object ConfigServiceAndHttpClientEnv {
    val layer: RLayer[ExecutionContextModule, ConfigServiceAndHttpClientEnv] =
      ConfigServiceEnv.layer ++ HttpClientModule.layer
  }
}
