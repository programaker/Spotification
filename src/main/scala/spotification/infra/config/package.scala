package spotification.infra

import spotification.core.config.AppConfig
import zio._
import pureconfig.ConfigSource
import spotification.core.config.ConfigModule.{ServerConfigService, SpotifyConfigService}

package object config {

  //==========
  // IntelliJ is complaining about:
  // import pureconfig.generic.auto._
  // import eu.timepit.refined.pureconfig._
  // not being used, but without them it does not compile
  //==========
  import pureconfig.generic.auto._
  import eu.timepit.refined.pureconfig._
  import Implicits._

  type AppConfigModule = Has[AppConfig]

  val appConfigLayer: Layer[Throwable, AppConfigModule] = ZLayer.fromEffect(
    IO.fromEither(ConfigSource.default.load[AppConfig])
      .mapError(_.prettyPrint())
      .absorbWith(new Exception(_))
  )

  val spotifyConfigLayer: ZLayer[AppConfigModule, Nothing, SpotifyConfigService] = ZLayer.fromService(_.spotify)
  val serverConfigLayer: ZLayer[AppConfigModule, Nothing, ServerConfigService] = ZLayer.fromService(_.server)

}
