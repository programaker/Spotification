package spotification

import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean.{And, Not}
import eu.timepit.refined.collection.{MaxSize, MinSize}
import eu.timepit.refined.generic.Equal
import eu.timepit.refined.numeric.Positive
import eu.timepit.refined.string.{HexStringSpec, MatchesRegex, Trimmed, Uri}
import eu.timepit.refined.auto._

package object domain {

  type NonBlankStringR = MinSize[1] And Not[MatchesRegex["""^\s+$"""]] And Trimmed
  type NonBlankString = String Refined NonBlankStringR

  // Size[N] refinement does not work for Strings,
  // but MinSize[N] and MaxSize[N] do somehow =S
  type StringLength[N] = MinSize[N] And MaxSize[N]

  type HexString32R = StringLength[32] And HexStringSpec
  type HexString32 = String Refined HexString32R

  type UriString = String Refined Uri

  type PositiveIntR = Positive
  type PositiveInt = Int Refined Positive

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

}
