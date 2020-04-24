package spotification.infra.config

import io.estatico.newtype.Coercible
import pureconfig.ConfigReader

private[config] trait NewTypeM {

  object newtype {
    implicit def coercibleConfigReader[A, B](
      implicit ev: Coercible[ConfigReader[A], ConfigReader[B]],
      A: ConfigReader[A]
    ): ConfigReader[B] = ev(A)
  }

}
