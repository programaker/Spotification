package spotification.common

import cats.syntax.show._
import eu.timepit.refined.api.Validate

import java.time.MonthDay
import java.time.format.DateTimeFormatter.ofPattern
import scala.util.Try

/**
 * Predicate that checks if a `String` is a parsable `MonthDay`
 * @tparam S string representing MonthDay date format (i.e. "MM-dd", "dd/MM", ...)
 */
final case class ValidMonthDay[S <: String]()
object ValidMonthDay {
  implicit def validMonthDayValidate[S <: String: ValueOf]: Validate.Plain[String, ValidMonthDay[S]] = {
    val format = valueOf[S]

    Validate.fromPredicate(
      s => Try(MonthDay.parse(s, ofPattern(format))).isSuccess,
      s => show"$s has format $format",
      ValidMonthDay()
    )
  }
}
