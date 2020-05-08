package spotification

import spotification.application.HttpServerApp.runHttpApp
import spotification.infra.concurrent.ExecutionContextZIO
import spotification.infra.config.ConfigZIO
import spotification.infra.httpclient.HttpClientZIO
import spotification.infra.httpserver.HttpServerZIO.HttpServerEnv
import spotification.presentation.PresentationZIO
import zio.{ZEnv, ZIO, ZLayer}

object Spotification extends zio.App {
  private val appLayer: ZLayer[Any, Throwable, HttpServerEnv] =
    (ExecutionContextZIO.layer >>> HttpClientZIO.layer) >>>
      (ConfigZIO.serverConfigLayer ++ ExecutionContextZIO.layer ++ PresentationZIO.layer)

  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] =
    runHttpApp.provideCustomLayer(appLayer).fold(_ => 1, _ => 0)
}
