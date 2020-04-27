package spotification.application

import spotification.core.config.ConfigModule
import spotification.core.spotify.authorization.AuthorizationModule
import spotification.infra.concurrent.ExecutionContextModule
import spotification.infra.httpclient.HttpClientModule
import spotification.infra.httpserver.{HttpServer, HttpServerEnv}
import zio.clock.Clock
import zio.{URIO, ZEnv, ZLayer}

object Application {
  private val appLayer: ZLayer[Any, Throwable, HttpServerEnv] =
    ExecutionContextModule.layer >>>
      (HttpClientModule.layer >>>
        (Clock.live ++ ConfigModule.layer ++ AuthorizationModule.layer))

  val runApplication: URIO[ZEnv, Int] =
    HttpServer.runHttpApp.provideSomeLayer[ZEnv](appLayer).fold(_ => 1, _ => 0)
}
