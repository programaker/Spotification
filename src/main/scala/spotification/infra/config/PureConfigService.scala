package spotification.infra.config

import pureconfig.ConfigSource
import spotification.core.config.{AppConfig, Config}
import zio.{IO, Task}

//==========
// IntelliJ is complaining about:
// import pureconfig.generic.auto._
// import eu.timepit.refined.pureconfig._
// not being used, but without them it does not compile
//==========
import pureconfig.generic.auto._
import eu.timepit.refined.pureconfig._

final class PureConfigService extends Config.Service {
  override val readConfig: Task[AppConfig] =
    IO.fromEither(ConfigSource.default.load[AppConfig])
      .mapError(_.prettyPrint())
      .absorbWith(new Exception(_))
}
