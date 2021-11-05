package spotification.common

import cats.syntax.show._
import eu.timepit.refined.api.Validate

import java.time.LocalDate
import java.time.format.DateTimeFormatter.ofPattern
import scala.util.Try

/**
 * Predicate that checks if a `String` is a parsable `Date`
 * @tparam S
 *   string representing Date date format (i.e. "yyyy-MM-dd", "dd/MM/yyy", ...)
 */
final case class ValidDate[S <: String]()
object ValidDate {
  implicit def validDateValidate[S <: String: ValueOf]: Validate.Plain[String, ValidDate[S]] = {
    val format = valueOf[S]

    Validate.fromPredicate(
      s => Try(LocalDate.parse(s, ofPattern(format))).isSuccess,
      s => show"$s has format $format",
      ValidDate()
    )
  }
}
