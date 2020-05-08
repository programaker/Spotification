package spotification.infra

import zio.{ULayer, ZEnv}

object BaseZIO {
  // Provides the minimum environment for all ZIO functions
  // This way, we can give app-wide access to features such as
  // Console and Logging without break type signatures
  //
  // This means, never return Task[A] from ZIO functions; the minimum
  // function result should be `RIO[BaseEnv, A]` or `URIO[BaseEnv, A]`
  type BaseEnv = ZEnv //TODO => add LogService

  val layer: ULayer[ZEnv] = ZEnv.live
}
