package spotification

import spotification.application.HttpServerApp.runHttpApp
import spotification.infra.ConfigServiceAndHttpClientEnv
import spotification.infra.concurrent.ExecutionContextModule
import spotification.infra.httpserver.HttpServerEnv
import zio.{ZEnv, ZIO, ZLayer}

object Spotification extends zio.App {
  private val appLayer: ZLayer[Any, Throwable, HttpServerEnv] =
    ExecutionContextModule.layer >>> ConfigServiceAndHttpClientEnv.layer >>> HttpServerEnv.layer

  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] =
    runHttpApp.provideCustomLayer(appLayer).fold(_ => 1, _ => 0)
}
