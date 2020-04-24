package spotification.infra

import spotification.core.config.Config
import zio._

package object config {

  val configLayer: Layer[Nothing, Config] = ZLayer.succeed(new PureConfigService)

}
