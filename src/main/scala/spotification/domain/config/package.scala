package spotification.domain

import zio.{Has, RIO, ZIO}

package object config {

  type Configuration = Has[ConfigService]
  val readConfig: RIO[Configuration, AppConfig] = ZIO.accessM(_.get.readConfig)

}
