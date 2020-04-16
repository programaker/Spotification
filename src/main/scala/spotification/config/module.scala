package spotification.config

import zio.{Has, RIO, Task, ZIO}

object module {

  type Configuration = Has[ConfigService]

  trait ConfigService {
    def readConfig: Task[AppConfig]
  }

  def readConfig: RIO[Configuration, AppConfig] = ZIO.accessM(_.get.readConfig)
}
