package spotification

import java.net.URLEncoder
import java.nio.charset.StandardCharsets.UTF_8

import cats.implicits._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean.{And, Not}
import eu.timepit.refined.collection.{MaxSize, MinSize}
import eu.timepit.refined.numeric.{NonNegative, Positive}
import eu.timepit.refined.string.{HexStringSpec, IPv4, MatchesRegex, Trimmed, Uri}

package object common {
  type NonBlankStringR = MinSize[1] And Not[MatchesRegex["""^\s+$"""]] And Trimmed
  type NonBlankString = String Refined NonBlankStringR

  // Size[N] refinement does not work for Strings,
  // but MinSize[N] and MaxSize[N] do somehow =S
  type StringLength[N] = MinSize[N] And MaxSize[N]

  type HexString32R = StringLength[32] And HexStringSpec
  type HexString32 = String Refined HexString32R

  type UriR = Uri
  type UriString = String Refined UriR
  type CurrentUri = UriString
  type NextUri = UriString

  type PositiveIntR = Positive
  type PositiveInt = Int Refined PositiveIntR

  type NonNegativeIntR = NonNegative
  type NonNegativeInt = Int Refined NonNegativeIntR

  type HostR = IPv4
  type Host = String Refined HostR

  // Some Spotify API's allow to select which fields we want to get
  // through a String like this one for playlist's tracks: "next,total,items.track.album(id,album_type)"
  type FieldsToReturnR = MatchesRegex["""^[a-z]([a-z_.()])+(\,([a-z_.()])+)*[a-z)]$"""]
  type FieldsToReturn = String Refined FieldsToReturnR

  // The base-62 identifier that you can find at the end of
  // the Spotify URI for an artist, track, album, playlist, etc
  type SpotifyIdR = MatchesRegex["^[0-9a-zA-Z]+$"]
  type SpotifyId = String Refined SpotifyIdR

  type ParamMap = Map[String, Option[String]]

  // HTTP4s Uri should be able to encode query params, but in my tests
  // URIs are not properly encoded:
  //
  // uri"https://foo.com".withQueryParam("redirect_uri", "https://bar.com")
  // > org.http4s.Uri = https://foo.com?redirect_uri=https%3A//bar.com <- did not encode `//`
  //
  // URLEncoder.encode("https://bar.com", UTF_8.toString)
  // > String = https%3A%2F%2Fbar.com <- encoded `//` correctly
  def encode(s: String): String = URLEncoder.encode(s, UTF_8)

  def makeQueryString(params: ParamMap): String =
    params.map {
      case (_, None)    => ""
      case (k, Some(v)) => show"$k=$v"
    } mkString "&"
}
