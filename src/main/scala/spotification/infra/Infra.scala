package spotification.infra

import eu.timepit.refined.api.{Refined, Validate}
import eu.timepit.refined.refineV
import zio.{IO, RIO, ZIO}

object Infra {
  def refineZIO[R, P]: PartialRefineZIO[R, P] = new PartialRefineZIO[R, P]
  def refineRIO[R, P]: PartialRefineRIO[R, P] = new PartialRefineRIO[R, P]

  final class PartialRefineZIO[R, P] {
    def apply[A](a: A)(implicit v: Validate[A, P]): ZIO[R, String, Refined[A, P]] =
      ZIO.fromFunctionM(_ => IO.fromEither(refineV[P](a)))
  }

  final class PartialRefineRIO[R, P] {
    def apply[A](a: A)(implicit v: Validate[A, P]): RIO[R, Refined[A, P]] =
      refineZIO[R, P](a).absorbWith(new Exception(_))
  }
}
