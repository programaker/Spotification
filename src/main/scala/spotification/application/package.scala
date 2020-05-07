package spotification

import spotification.application.AuthorizationModule.AuthorizationService
import spotification.application.ConfigModule.ConfigServices

package object application {
  type ApplicationServices = ConfigServices with AuthorizationService

  // Provides the minimum environment for all ZIO functions
  // This way, we can give app-wide access to features such as
  // Console and Logging without break type signatures
  //
  // This means, never return Task[A] from ZIO functions; the minimum
  // function result should be `RIO[BaseEnv, A]` or `URIO[BaseEnv, A]`
  type BaseEnv = zio.ZEnv //TODO => add LogService
}
