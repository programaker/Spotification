package spotification.common

import eu.timepit.refined.api.Validate

import java.time.LocalDate
import java.time.format.DateTimeFormatter.ofPattern

/**
 * Predicate that checks if a `String` is a parsable `Date`
 * @tparam S string representing Date date format (i.e. "yyyy-MM-dd", "dd/MM/yyy", ...)
 */
final case class ValidDate[S <: String]()
object ValidDate {
  implicit def validDateValidate[S <: String: ValueOf]: Validate.Plain[String, ValidDate[S]] =
    Validate.fromPartial(LocalDate.parse(_, ofPattern(valueOf[S])), "ValidDate", ValidDate())
}
