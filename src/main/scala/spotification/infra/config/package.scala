package spotification.infra

import spotification.core.config.{AppConfig, ServerConfigModule, SpotifyConfigModule}
import zio._
import pureconfig.ConfigSource

package object config extends NewTypeM {

  //==========
  // IntelliJ is complaining about:
  // import pureconfig.generic.auto._
  // import eu.timepit.refined.pureconfig._
  // not being used, but without them it does not compile
  //==========
  import pureconfig.generic.auto._
  import eu.timepit.refined.pureconfig._
  import newtype._

  type AppConfigModule = Has[AppConfig]

  val appConfigLayer: Layer[Throwable, AppConfigModule] = ZLayer.fromEffect(
    IO.fromEither(ConfigSource.default.load[AppConfig])
      .mapError(_.prettyPrint())
      .absorbWith(new Exception(_))
  )

  val spotifyConfigLayer: ZLayer[AppConfigModule, Nothing, SpotifyConfigModule] = ZLayer.fromService(_.spotify)
  val serverConfigLayer: ZLayer[AppConfigModule, Nothing, ServerConfigModule] = ZLayer.fromService(_.server)

}
