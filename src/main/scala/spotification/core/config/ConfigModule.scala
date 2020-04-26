package spotification.core.config

import spotification.infra.config.PureConfigService
import zio.{Has, Layer, RIO, Task, ZIO, ZLayer}

object ConfigModule {
  type ConfigService = Has[ConfigModule.Service]

  trait Service {
    def readConfig: Task[AppConfig]
  }

  val readConfig: RIO[ConfigService, AppConfig] = ZIO.accessM(_.get.readConfig)
  val layer: Layer[Throwable, ConfigService] = ZLayer.succeed(PureConfigService)
}
