package spotification.infra

import eu.timepit.refined.api.{Refined, Validate}
import eu.timepit.refined.refineV
import zio.{IO, RIO, ZIO}

object Infra {
  def refineZIO[P, R, A](a: A)(implicit v: Validate[A, P]): ZIO[R, String, Refined[A, P]] =
    ZIO.fromFunctionM(_ => IO.fromEither(refineV[P](a)))

  def refineRIO[P, R, A](a: A)(implicit v: Validate[A, P]): RIO[R, Refined[A, P]] =
    refineZIO[P, R, A](a).absorbWith(new Exception(_))
}
