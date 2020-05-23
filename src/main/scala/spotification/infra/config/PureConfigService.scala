package spotification.infra.config

import pureconfig.ConfigSource
import spotification.domain.config.AppConfig
import zio.{IO, Task}

//==========
// IntelliJ is complaining about:
// import pureconfig.generic.auto._
// import eu.timepit.refined.pureconfig._
// not being used, but without them it does not compile
//==========
import pureconfig.generic.auto._
import eu.timepit.refined.pureconfig._
import Config.Implicits._

object PureConfigService {
  val readAppConfig: Task[AppConfig] =
    IO.fromEither(ConfigSource.default.load[AppConfig])
      .mapError(_.prettyPrint())
      .absorbWith(new Exception(_))
}
