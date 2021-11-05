package spotification

import cats.effect.kernel.Async
import cats.effect.std.Dispatcher
import cats.syntax.functor._
import eu.timepit.refined.api.{Refined, Validate}
import spotification.common.{RefinementError, refineE}
import zio._
import zio.blocking.Blocking
import zio.clock.Clock

import scala.annotation.nowarn

package object effect {
  def refineZIO[R, P]: PartialRefineZIO[R, P] = new PartialRefineZIO
  final class PartialRefineZIO[R, P] {
    def apply[A](a: A)(implicit v: Validate[A, P]): ZIO[R, RefinementError, Refined[A, P]] =
      ZIO.fromFunctionM(_ => IO.fromEither(refineE[P](a)))
  }

  def refineRIO[R, P]: PartialRefineRIO[R, P] = new PartialRefineRIO
  final class PartialRefineRIO[R, P] {
    def apply[A](a: A)(implicit v: Validate[A, P]): RIO[R, Refined[A, P]] =
      refineZIO[R, P](a)
  }

  def refineTask[P]: PartialRefineTask[P] = new PartialRefineTask
  final class PartialRefineTask[P] {
    def apply[A](a: A)(implicit v: Validate[A, P]): Task[Refined[A, P]] =
      Task.fromEither(refineE[P](a))
  }

  def eitherToRIO[R, B](either: Either[Throwable, B]): RIO[R, B] =
    RIO.fromFunctionM((_: R) => Task.fromEither(either))

  def accessRIO[A: Tag]: RIO[Has[A], A] =
    ZIO.access(_.get)

  @nowarn //I've failed to solve the "unused context bound" warning =/
  def accessServiceFunction[A: Tag, B: Tag](a: A): RIO[Has[A => Task[B]], B] =
    ZIO.accessM(_.get.apply(a))

  def unitRIO[R]: RIO[R, Unit] = RIO.unit

  def managedTaskDispatcher(implicit A: Async[Task]): TaskManaged[Dispatcher[Task]] =
    makeDispatcher[Task].toManaged_

  def makeDispatcher[F[_]: Async]: F[Dispatcher[F]] =
    Dispatcher[F].allocated.map { case (dispatcher, _) => dispatcher }

  def managedZIORuntime[R]: RManaged[R, Runtime[R]] = ZIO.runtime[R].toManaged_
}
