package spotification.core

import zio.{Has, RIO, Task, ZIO}

package object config {

  type Config = Has[Config.Service]

  object Config {
    trait Service {
      def readConfig: Task[AppConfig]
    }

    val readConfig: RIO[Config, AppConfig] =
      ZIO.accessM(_.get.readConfig)
  }

}
