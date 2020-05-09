package spotification.domain.spotify

import io.estatico.newtype.macros.newtype
import spotification.domain.SpotifyId

package object playlist {
  @newtype case class PlaylistId(value: SpotifyId)
}
