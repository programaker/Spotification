package spotification
import spotification.core.config.ConfigModule
import spotification.core.spotify.authorization.AuthorizationModule
import spotification.infra.concurrent.ExecutionContextModule
import spotification.infra.httpclient.HttpClientModule
import spotification.infra.httpserver.{HttpServer, HttpServerEnv}
import zio.clock.Clock
import zio.{ZEnv, ZIO, ZLayer}

object Spotification extends zio.App {
  private val appLayer: ZLayer[Any, Throwable, HttpServerEnv] =
    ExecutionContextModule.layer >>>
      (HttpClientModule.layer >>>
        (Clock.live ++ ConfigModule.layer ++ AuthorizationModule.layer))

  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] =
    HttpServer.runHttpApp.provideSomeLayer[ZEnv](appLayer).fold(_ => 1, _ => 0)
}
