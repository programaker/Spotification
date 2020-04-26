package spotification

import spotification.core.config.ServerConfigModule
import spotification.presentation.{PresentationEnv, PresentationModule}
import zio.clock.Clock
import zio.interop.catz._
import zio.{RIO, ZIO}

package object application extends ApplicationM {

  type AppEnv = ServerConfigModule with PresentationEnv with Clock
  type AppIO[A] = RIO[AppEnv, A]

  val runApp: RIO[AppEnv with PresentationModule[AppIO], Unit] = ZIO.runtime[AppEnv].flatMap { implicit appEnv =>
    for {
      routes       <- PresentationModule.allRoutes[AppIO]
      serverConfig <- ServerConfigModule.serverConfig
      _            <- runHttpServer[AppIO](serverConfig, addLogger(httpApp(routes)))
    } yield ()
  }

}
