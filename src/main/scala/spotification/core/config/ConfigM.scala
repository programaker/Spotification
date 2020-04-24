package spotification.core.config

import zio.{Has, RIO, Task, ZIO}

private[config] trait ConfigM {

  type Config = Has[Config.Service]
  object Config {
    trait Service {
      def readConfig: Task[AppConfig]
    }

    val readConfig: RIO[Config, AppConfig] =
      ZIO.accessM(_.get.readConfig)
  }

}
