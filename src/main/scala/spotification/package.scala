import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean.{And, Not}
import eu.timepit.refined.collection.{MinSize, Size}
import eu.timepit.refined.string.{HexStringSpec, MatchesRegex, Trimmed}

package object spotification {
  type NonBlankStringR = MinSize[1] And Not[MatchesRegex["""^\s+$"""]] And Trimmed
  type NonBlankString = String Refined NonBlankStringR

  type HexString32R = Size[32] And HexStringSpec
  type HexString32 = String Refined HexString32R

  type UriString = String Refined UriString
}
