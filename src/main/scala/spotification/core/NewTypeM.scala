package spotification.core

import cats._
import io.estatico.newtype.Coercible

private[core] trait NewTypeM {

  object newtype {
    /* If we have a `TypeClass` instance for the Repr type A, derive a `TypeClass` instance for the NewType B */
    implicit def coercibleEq[A, B](implicit ev: Coercible[Eq[A], Eq[B]], A: Eq[A]): Eq[B] = ev(A)
    implicit def coercibleShow[A, B](implicit ev: Coercible[Show[A], Show[B]], A: Show[A]): Show[B] = ev(A)
  }

}
