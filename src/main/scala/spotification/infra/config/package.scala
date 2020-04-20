package spotification.infra

import spotification.domain.config.Configuration
import zio.{Layer, ZLayer}

package object config {

  val ConfigLayer: Layer[Nothing, Configuration] = ZLayer.succeed(new PureConfigService)

}
