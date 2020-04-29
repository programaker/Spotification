package spotification
import spotification.core.CoreModule
import spotification.core.CoreModule.CoreServices
import spotification.infra.InfraModule
import spotification.infra.httpserver.HttpServer
import zio.{ZEnv, ZIO, ZLayer}

object Spotification extends zio.App {
  private val appLayer: ZLayer[Any, Throwable, CoreServices] =
    InfraModule.layer >>> CoreModule.layer

  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] =
    HttpServer.runHttpApp.provideCustomLayer(appLayer).fold(_ => 1, _ => 0)
}
