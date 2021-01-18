package spotification

import cats.Show
import eu.timepit.refined.cats.refTypeShow
import io.estatico.newtype.macros.newtype
import spotification.common.UriString

package object me {
  @newtype case class MeApiUri(value: UriString)
  object MeApiUri {
    implicit val MeApiUriShow: Show[MeApiUri] = deriving
  }
}
