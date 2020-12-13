package spotification

import eu.timepit.refined.api.Refined
import eu.timepit.refined.generic.Equal
import eu.timepit.refined.auto._

package object follow {
  // currently only `artist` is supported by Spotify.
  type FollowTypeArtist = "artist"
  type FollowTypeR = Equal[FollowTypeArtist]
  type FollowType = String Refined FollowTypeR
  object FollowType {
    val Artist: FollowType = "artist"
  }
}