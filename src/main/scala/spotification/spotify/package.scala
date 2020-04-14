package spotification

import java.net.URLEncoder
import java.nio.charset.StandardCharsets.UTF_8
import java.util.Base64

import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean.{And, Not}
import eu.timepit.refined.collection.{MinSize, Size}
import eu.timepit.refined.numeric.Positive
import eu.timepit.refined.string.{HexStringSpec, MatchesRegex, Trimmed, Uri}
import eu.timepit.refined.auto._
import spotification.spotify.authorization.{AccessToken, Credentials}

package object spotify {

  type NonBlankStringR = MinSize[1] And Not[MatchesRegex["""^\s+$"""]] And Trimmed
  type NonBlankString = String Refined NonBlankStringR

  type HexString32R = Size[32] And HexStringSpec
  type HexString32 = String Refined HexString32R

  type UriString = String Refined Uri

  type PositiveIntR = Positive
  type PositiveInt = Int Refined Positive

  // HTTP4s Uri should be able to encode query params, but in my tests
  // URIs are not properly encoded:
  //
  // uri"https://foo.com".withQueryParam("redirect_uri", "https://bar.com")
  // > org.http4s.Uri = https://foo.com?redirect_uri=https%3A//bar.com <- did not encode `//`
  //
  // URLEncoder.encode("https://bar.com", UTF_8.toString)
  // > String = https%3A%2F%2Fbar.com <- encoded `//` correctly
  def encode: String => String =
    URLEncoder.encode(_, UTF_8.toString)

  def base64Credentials(credentials: Credentials): String =
    Base64.getEncoder.encodeToString(s"${credentials.clientId}:${credentials.clientSecret}".getBytes(UTF_8))

  def authorizationBasicHeader(credentials: Credentials): String =
    s"Authorization: Basic ${base64Credentials(credentials)}"

  def authorizationBearerHeader(accessToken: AccessToken): String =
    s"Authorization: Bearer ${accessToken.value}"

  def toQueryStringParams[T: ToQueryStringParams](t: T): Map[String, String] =
    ToQueryStringParams[T].convert(t)

}
