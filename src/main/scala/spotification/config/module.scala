package spotification.config

import zio.{Has, Layer, RIO, Task, ZIO, ZLayer}

/** ZIO module for Configuration */
object module {

  type Configuration = Has[ConfigService]

  trait ConfigService {
    def readConfig: Task[AppConfig]
  }

  val readConfig: RIO[Configuration, AppConfig] =
    ZIO.accessM(_.get.readConfig)

  val configLayer: Layer[Nothing, Configuration] =
    ZLayer.succeed(new PureConfigService)

}
