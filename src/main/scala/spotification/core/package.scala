package spotification

import java.net.URLEncoder
import java.nio.charset.StandardCharsets.UTF_8
import java.util.Base64

import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean.{And, Not}
import eu.timepit.refined.collection.{MaxSize, MinSize}
import eu.timepit.refined.numeric.Positive
import eu.timepit.refined.string.{HexStringSpec, MatchesRegex, Trimmed, Uri}

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

  // HTTP4s Uri should be able to encode query params, but in my tests
  // URIs are not properly encoded:
  //
  // uri"https://foo.com".withQueryParam("redirect_uri", "https://bar.com")
  // > org.http4s.Uri = https://foo.com?redirect_uri=https%3A//bar.com <- did not encode `//`
  //
  // URLEncoder.encode("https://bar.com", UTF_8.toString)
  // > String = https%3A%2F%2Fbar.com <- encoded `//` correctly
  val encode: String => String = URLEncoder.encode(_, UTF_8)

  def base64(s: String): String = Base64.getEncoder.encodeToString(s.getBytes(UTF_8))

}
