package spotification

import eu.timepit.refined.api.{Refined, Validate}
import eu.timepit.refined.refineV
import zio.{Has, IO, RIO, Tag, Task, ZIO}

package object effect {
  def refineZIO[R, P]: PartialRefineZIO[R, P] = new PartialRefineZIO
  final class PartialRefineZIO[R, P] {
    def apply[A](a: A)(implicit v: Validate[A, P]): ZIO[R, String, Refined[A, P]] =
      ZIO.fromFunctionM(_ => IO.fromEither(refineV[P](a)))
  }

  def refineRIO[R, P]: PartialRefineRIO[R, P] = new PartialRefineRIO
  final class PartialRefineRIO[R, P] {
    def apply[A](a: A)(implicit v: Validate[A, P]): RIO[R, Refined[A, P]] =
      refineZIO[R, P](a).absorbWith(new Exception(_))
  }

  def refineTask[P]: PartialRefineTask[P] = new PartialRefineTask
  final class PartialRefineTask[P] {
    def apply[A](a: A)(implicit v: Validate[A, P]): Task[Refined[A, P]] =
      leftStringEitherToTask(refineV[P](a))
  }

  def eitherToTask[A, B](either: Either[A, B])(f: A => Throwable): Task[B] =
    IO.fromEither(either).absorbWith(f)

  def leftStringEitherToTask[B](either: Either[String, B]): Task[B] =
    eitherToTask(either)(new Exception(_))

  def leftStringEitherToRIO[R, B](either: Either[String, B]): RIO[R, B] =
    RIO.fromFunctionM((_: R) => leftStringEitherToTask(either))

  def accessRIO[A: Tag]: RIO[Has[A], A] =
    ZIO.access(_.get)
}
