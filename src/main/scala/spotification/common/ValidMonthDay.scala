package spotification.common

import eu.timepit.refined.api.Validate

import java.time.MonthDay
import java.time.format.DateTimeFormatter.ofPattern

/**
 * Predicate that checks if a `String` is a parsable `MonthDay`
 * @tparam S string representing MonthDay date format (i.e. "MM-dd", "dd/MM", ...)
 */
final case class ValidMonthDay[S <: String]()
object ValidMonthDay {
  implicit def validMonthDayValidate[S <: String: ValueOf]: Validate.Plain[String, ValidMonthDay[S]] =
    Validate.fromPartial(MonthDay.parse(_, ofPattern(valueOf[S])), "ValidMonthDay", ValidMonthDay())
}
