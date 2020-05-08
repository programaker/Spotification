package spotification

import spotification.application.HttpServerApp.runHttpApp
import spotification.infra.concurrent.ExecutionContextModule
import spotification.infra.config.ServerConfigModule
import spotification.infra.httpclient.HttpClientModule
import spotification.infra.httpserver.HttpServerModule
import spotification.presentation.PresentationModule
import zio.{ZEnv, ZIO, ZLayer}

object Spotification extends zio.App {
  private val appLayer: ZLayer[Any, Throwable, HttpServerModule] =
    (ExecutionContextModule.layer >>> HttpClientModule.layer) >>>
      (ServerConfigModule.layer ++ ExecutionContextModule.layer ++ PresentationModule.layer)

  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] =
    runHttpApp.provideCustomLayer(appLayer).fold(_ => 1, _ => 0)
}
