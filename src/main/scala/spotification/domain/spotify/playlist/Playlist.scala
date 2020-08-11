package spotification.domain.spotify.playlist

import cats.implicits.showInterpolator
import eu.timepit.refined.refineV
import spotification.domain.{UriR, UriString}

object Playlist {
  def playlistTracksUri(playlistApiUri: PlaylistApiUri, playlistId: PlaylistId): Either[String, UriString] =
    refineV[UriR](show"$playlistApiUri/$playlistId/tracks")
}
