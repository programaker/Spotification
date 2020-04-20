package spotification.domain

import cats.implicits._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean.Or
import eu.timepit.refined.generic.Equal
import eu.timepit.refined._
import eu.timepit.refined.auto._

object scope {

  type PlaylistScopeR =
    Equal["playlist-read-collaborative"] Or
      Equal["playlist-modify-public"] Or
      Equal["playlist-read-private"] Or
      Equal["playlist-modify-private"]

  type ScopeR = PlaylistScopeR //we can add more scopes later
  type Scope = String Refined ScopeR

  def parseScope(rawScope: SpaceSeparatedString): Either[String, List[Scope]] =
    rawScope.split("\\s").toList.map(refineV[ScopeR](_)).sequence[Either[String, *], Scope]

  def joinScopes(scopes: List[Scope]): Either[String, SpaceSeparatedString] =
    refineV[SpaceSeparatedStringR](scopes.mkString(" "))

}
