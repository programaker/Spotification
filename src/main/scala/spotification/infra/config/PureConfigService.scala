package spotification.infra.config

import pureconfig.ConfigSource
import spotification.domain.config.{AppConfig, ConfigService}
import zio.{IO, Task}

//==========
// IntelliJ is complaining about:
// import pureconfig.generic.auto._
// import eu.timepit.refined.pureconfig._
// not being used, but without them it does not compile
//==========
import pureconfig.generic.auto._
import eu.timepit.refined.pureconfig._

final class PureConfigService extends ConfigService {
  override def readConfig: Task[AppConfig] =
    IO.fromEither(ConfigSource.default.load[AppConfig])
      .mapError(_.prettyPrint())
      .absorbWith(new Exception(_))
}
