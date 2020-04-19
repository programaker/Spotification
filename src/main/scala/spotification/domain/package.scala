package spotification

import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean.{And, Not, Or}
import eu.timepit.refined.collection.{MaxSize, MinSize}
import eu.timepit.refined.generic.Equal
import eu.timepit.refined.numeric.Positive
import eu.timepit.refined.string.{HexStringSpec, MatchesRegex, Trimmed, Uri}

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

  type ResponseTypeR = Equal["code"] //it's the only one that appeared until now
  type ResponseType = String Refined ResponseTypeR

  type GrantTypeR = Equal["authorization_code"] Or Equal["refresh_token"]
  type GrantType = String Refined GrantTypeR

  type TokenTypeR = Equal["Bearer"]
  type TokenType = String Refined TokenTypeR

}
