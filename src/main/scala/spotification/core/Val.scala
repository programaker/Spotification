package spotification.core

import cats.implicits._
import cats.{Eq, Show}
import eu.timepit.refined.api.Refined
import eu.timepit.refined.cats._

/** Represents a wrapper for a value of type A.
 * It acts like a "newtype", but it's actually just a tiny type =/
 *
 * So, why not just use the "io.estatico.newtype" library. It helps
 * creating true newtypes with zero cost (no object allocation)?
 *
 * The problem is that libs like Circe and PureConfig can't automatically
 * derive typeclass instances for @newtypes, then I prefer to pay the
 * (I believe small) memory cost of tiny types than lose the productivity
 * and maintainability of auto derivation */
abstract class Val[+A: Show] extends Product with Serializable {
  def value: A
  override def toString: String = show"$value"
}

object Val {

  type ValR[A, P] = Val[Refined[A, P]]

  /* Eq instances for Val */
  implicit def valEq[A: Eq]: Eq[Val[A]] = (v1: Val[A], v2: Val[A]) => v1.value === v2.value
  implicit def refinedValEq[A: Eq, P]: Eq[ValR[A, P]] = (v1: ValR[A, P], v2: ValR[A, P]) => v1.value === v2.value

  /* Show instances for Val */
  implicit def valShow[A: Show]: Show[Val[A]] = (v: Val[A]) => v.value.show
  implicit def refinedValShow[A: Show, P]: Show[ValR[A, P]] = (v: ValR[A, P]) => v.value.show

}
