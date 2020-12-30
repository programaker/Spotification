package spotification

import cats.{Eq, Foldable, Show}

import java.net.URLEncoder
import java.nio.charset.StandardCharsets.UTF_8
import cats.syntax.show._
import cats.syntax.eq._
import cats.syntax.foldable._
import eu.timepit.refined.auto._
import eu.timepit.refined.api.{Refined, Validate}
import eu.timepit.refined.boolean.{And, Not}
import eu.timepit.refined.cats.refTypeShow
import eu.timepit.refined.collection.{MaxSize, MinSize}
import eu.timepit.refined.numeric.{NonNegative, Positive}
import eu.timepit.refined.refineV
import eu.timepit.refined.string.{HexStringSpec, IPv4, MatchesRegex, Trimmed, Uri}
import io.estatico.newtype.macros.newtype

import java.time.{LocalDate, MonthDay => JMonthDay}
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ofPattern

package object common {
  type NonBlankStringR = MinSize[1] And Not[MatchesRegex["""^\s+$"""]] And Trimmed
  type NonBlankString = String Refined NonBlankStringR

  // Size[N] refinement does not work for Strings,
  // but MinSize[N] and MaxSize[N] do somehow =S
  type StringLength[N] = MinSize[N] And MaxSize[N]

  type HexString32R = StringLength[32] And HexStringSpec
  type HexString32 = String Refined HexString32R

  type UriStringR = Uri
  type UriString = String Refined UriStringR
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

  type DayMonthStringR = ValidMonthDay["dd-MM"]
  type DayMonthString = String Refined DayMonthStringR
  object DayMonthString {
    val DashFormatter: DateTimeFormatter = ofPattern("dd-MM")
    val SlashFormatter: DateTimeFormatter = ofPattern("dd/MM")
  }

  type YearMonthDayStringR = ValidDate["yyyy-MM-dd"]
  type YearMonthDayString = String Refined YearMonthDayStringR
  object YearMonthDayString {
    val DashFormatter: DateTimeFormatter = ofPattern("yyyy-MM-dd")
  }

  @newtype case class MonthDay(value: JMonthDay)
  object MonthDay {
    implicit val MonthDayShow: Show[MonthDay] = _.value.format(DayMonthString.SlashFormatter)
    implicit val MonthDayEq: Eq[MonthDay] = (md1, md2) => md1.value.compareTo(md2.value) === 0

    def fromDayMonthString(s: DayMonthString): MonthDay =
      MonthDay(JMonthDay.parse(s, DayMonthString.DashFormatter))

    def fromYearMonthDayString(s: YearMonthDayString): MonthDay =
      MonthDay(JMonthDay.from(LocalDate.parse(s, YearMonthDayString.DashFormatter)))
  }

  // HTTP4s Uri should be able to encode query params, but in my tests
  // URIs are not properly encoded:
  //
  // uri"https://foo.com".withQueryParam("redirect_uri", "https://bar.com")
  // > org.http4s.Uri = https://foo.com?redirect_uri=https%3A//bar.com <- did not encode `//`
  //
  // URLEncoder.encode("https://bar.com", UTF_8.toString)
  // > String = https%3A%2F%2Fbar.com <- encoded `//` correctly
  def encodeUrl(s: String): String = URLEncoder.encode(s, UTF_8)

  def makeQueryString(params: ParamMap): String =
    params.map {
      case (_, None)    => ""
      case (k, Some(v)) => show"$k=$v"
    } mkString "&"

  def joinRefinedStrings[F[_]: Foldable, P1, P2](
    strings: F[Refined[String, P1]],
    separator: String
  )(implicit
    v: Validate[String, P2]
  ): Either[String, Refined[String, P2]] =
    refineV[P2](strings.mkString_(separator))

  def addRefinedStringParam[P](paramName: String, params: ParamMap, string: Refined[String, P]): ParamMap =
    params + (paramName -> Some(encodeUrl(string)))
}
