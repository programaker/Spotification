package spotification

import java.net.URLEncoder
import java.nio.charset.StandardCharsets.UTF_8
import java.util.Base64

import eu.timepit.refined.api.{Refined, Validate}
import eu.timepit.refined.boolean.{And, Not}
import eu.timepit.refined.collection.{MaxSize, MinSize}
import eu.timepit.refined.numeric.Positive
import eu.timepit.refined.refineV
import eu.timepit.refined.string.{HexStringSpec, IPv4, MatchesRegex, Trimmed, Uri}
import zio.{IO, RIO, ZIO}

package object core extends NewTypeM {

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

  def refineZIO[P, R, A](a: A)(implicit v: Validate[A, P]): ZIO[R, String, Refined[A, P]] =
    ZIO.fromFunctionM(_ => IO.fromEither(refineV[P](a)))

  def refineRIO[P, R, A](a: A)(implicit v: Validate[A, P]): RIO[R, Refined[A, P]] =
    refineZIO[P, R, A](a).absorbWith(new Exception(_))

}
