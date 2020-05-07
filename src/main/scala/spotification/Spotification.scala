package spotification

import spotification.application.{ApplicationModule, ApplicationServices}
import spotification.infra.InfraModule
import spotification.infra.httpserver.HttpServer
import zio.{ZEnv, ZIO, ZLayer}

object Spotification extends zio.App {
  private val appLayer: ZLayer[Any, Throwable, ApplicationServices] =
    InfraModule.layer >>> ApplicationModule.layer

  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] =
    HttpServer.runHttpApp.provideCustomLayer(appLayer).fold(_ => 1, _ => 0)
}
