package spotification

import eu.timepit.refined.api.{Refined, Validate}
import eu.timepit.refined.refineV
import zio.{IO, RIO, Task, ZIO, Tag, Has}

package object effect {
  def refineZIO[R, P]: PartialRefineZIO[R, P] = new PartialRefineZIO[R, P]
  final class PartialRefineZIO[R, P] {
    def apply[A](a: A)(implicit v: Validate[A, P]): ZIO[R, String, Refined[A, P]] =
      ZIO.fromFunctionM(_ => IO.fromEither(refineV[P](a)))
  }

  def refineRIO[R, P]: PartialRefineRIO[R, P] = new PartialRefineRIO[R, P]
  final class PartialRefineRIO[R, P] {
    def apply[A](a: A)(implicit v: Validate[A, P]): RIO[R, Refined[A, P]] =
      refineZIO[R, P](a).absorbWith(new Exception(_))
  }

  def eitherToTask[L, R](either: Either[L, R])(f: L => Throwable): Task[R] =
    IO.fromEither(either).absorbWith(f)

  def leftStringEitherToTask[R](either: Either[String, R]): Task[R] =
    eitherToTask(either)(new Exception(_))

  def leftStringEitherToRIO[R, B](either: Either[String, B]): RIO[R, B] =
    RIO.fromFunctionM((_: R) => leftStringEitherToTask(either))

  def accessRIO[A: Tag]: RIO[Has[A], A] = 
    ZIO.access(_.get)  
}
