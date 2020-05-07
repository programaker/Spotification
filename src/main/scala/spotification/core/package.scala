package spotification

import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean.{And, Not}
import eu.timepit.refined.collection.{MaxSize, MinSize}
import eu.timepit.refined.numeric.Positive
import eu.timepit.refined.string.{HexStringSpec, IPv4, MatchesRegex, Trimmed, Uri}

package object core {
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

  type SpaceSeparatedStringR = MatchesRegex["""^(\w|[-])+(\s(\w|[-])+)*$"""]
  type SpaceSeparatedString = String Refined SpaceSeparatedStringR

  type HostR = IPv4
  type Host = String Refined HostR
}
