package spotification

import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean.{And, Not}
import eu.timepit.refined.collection.{MaxSize, MinSize}
import eu.timepit.refined.numeric.{NonNegative, Positive}
import eu.timepit.refined.string.{HexStringSpec, IPv4, MatchesRegex, Trimmed, Uri}

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
}
