package spotification.domain.spotify

import cats.Show
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.boolean.Or
import eu.timepit.refined.generic.Equal
import eu.timepit.refined.refineV
import eu.timepit.refined.cats._
import io.estatico.newtype.macros.newtype
import io.estatico.newtype.ops._
import spotification.domain._
import cats.implicits._
import eu.timepit.refined.string.MatchesRegex

package object authorization {
  type AuthorizationResponseTypeR = Equal["code"] //it's the only one that appeared until now
  type AuthorizationResponseType = String Refined AuthorizationResponseTypeR
  object AuthorizationResponseType {
    val Code: AuthorizationResponseType = "code"
  }

  type AccessTokenGrantTypeR = Equal["authorization_code"]
  type AccessTokenGrantType = String Refined AccessTokenGrantTypeR
  object AccessTokenGrantType {
    val AuthorizationCode: AccessTokenGrantType = "authorization_code"
  }

  type RefreshTokenGrantTypeR = Equal["refresh_token"]
  type RefreshTokenGrantType = String Refined RefreshTokenGrantTypeR
  object RefreshTokenGrantType {
    val RefreshToken: RefreshTokenGrantType = "refresh_token"
  }

  type TokenTypeR = Equal["Bearer"]
  type TokenType = String Refined TokenTypeR
  object TokenType {
    val Bearer: TokenType = "Bearer"
  }

  // ScopeString = space-separated kebab-case strings
  // ex: "playlist-read-private playlist-modify-private playlist-modify-public"
  type ScopeStringR = MatchesRegex["""^[a-z]([a-z-])+(\s([a-z-])+)*[a-z]$"""]
  type ScopeString = String Refined ScopeStringR

  type PlaylistScopeR =
    Equal["playlist-read-collaborative"] Or
      Equal["playlist-modify-public"] Or
      Equal["playlist-read-private"] Or
      Equal["playlist-modify-private"]

  type ScopeR = PlaylistScopeR //we can add more scopes later
  type Scope = String Refined ScopeR
  object Scope {
    def parseScope(rawScope: ScopeString): Either[String, List[Scope]] =
      rawScope.split("\\s").toList.map(refineV[ScopeR](_)).sequence[Either[String, *], Scope]

    def joinScopes(scopes: List[Scope]): Either[String, ScopeString] =
      refineV[ScopeStringR](scopes.mkString(" "))
  }

  @newtype case class AccessToken(value: NonBlankString)
  @newtype case class RefreshToken(value: NonBlankString)

  @newtype case class ClientId(value: HexString32)
  object ClientId {
    implicit val clientIdShow: Show[ClientId] = implicitly[Show[HexString32]].coerce
  }

  @newtype case class ClientSecret(value: HexString32)
  object ClientSecret {
    implicit val clientSecretShow: Show[ClientSecret] = implicitly[Show[HexString32]].coerce
  }
}
