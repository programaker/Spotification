package spotification.config

import spotification.config.domain.AppConfig
import zio.{Layer, ZLayer}
import zio.Task
import zio.Has
import zio.RIO
import zio.ZIO

package object infra {

  type Config = Has[Config.Service]

  object Config {
    trait Service {
      def readConfig: Task[AppConfig]
    }

    val readConfig: RIO[Config, AppConfig] =
      ZIO.accessM(_.get.readConfig)

    val ConfigLayer: Layer[Nothing, Config] =
      ZLayer.succeed(new PureConfigService)
  }

}
