package spotification.core.spotify.authorization

import eu.timepit.refined.refineV
import eu.timepit.refined.auto._
import cats.implicits._
import spotification.core.{SpaceSeparatedString, SpaceSeparatedStringR}

object Scope {
  def parseScope(rawScope: SpaceSeparatedString): Either[String, List[Scope]] =
    rawScope.split("\\s").toList.map(refineV[ScopeR](_)).sequence[Either[String, *], Scope]

  def joinScopes(scopes: List[Scope]): Either[String, SpaceSeparatedString] =
    refineV[SpaceSeparatedStringR](scopes.mkString(" "))
}
