package spotification.core.spotify

import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.boolean.Or
import eu.timepit.refined.generic.Equal
import io.estatico.newtype.macros.newtype
import spotification.core._
import spotification.core.Implicits._

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

  type PlaylistScopeR =
    Equal["playlist-read-collaborative"] Or
      Equal["playlist-modify-public"] Or
      Equal["playlist-read-private"] Or
      Equal["playlist-modify-private"]

  type ScopeR = PlaylistScopeR //we can add more scopes later
  type Scope = String Refined ScopeR

  @newtype case class AccessToken(value: NonBlankString)
  @newtype case class RefreshToken(value: NonBlankString)

  @newtype case class ClientId(value: HexString32)
  @newtype case class ClientSecret(value: HexString32)
}
