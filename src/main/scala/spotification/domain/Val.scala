package spotification.domain

import cats.{Eq, Show}
import cats.implicits._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.cats._

/** Represents a wrapper for a value of type A */
trait Val[+A] {
  def value: A
}

object Val {

  type ValR[A, P] = Val[Refined[A, P]]

  /* Eq instances for Vals */
  implicit def valEq[A: Eq]: Eq[Val[A]] = (v1: Val[A], v2: Val[A]) => v1.value === v2.value
  implicit def refinedValEq[A: Eq, P]: Eq[ValR[A, P]] = (v1: ValR[A, P], v2: ValR[A, P]) => v1 === v2

  /* Show instances for Vals */
  implicit def valShow[A: Show]: Show[Val[A]] = (v: Val[A]) => v.value.show
  implicit def refinedValShow[A: Show, P]: Show[ValR[A, P]] = (v: ValR[A, P]) => v.value.show

  /* Implicit conversions to extract the value from a Val */
  implicit def extractValue[A]: Val[A] => A = _.value
  implicit def extractRefinedValue[A, P]: ValR[A, P] => A = _.value.value

}
