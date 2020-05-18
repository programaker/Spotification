package spotification.domain.spotify

import cats.Show
import cats.implicits._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean.And
import io.estatico.newtype.macros.newtype
import io.estatico.newtype.ops._
import eu.timepit.refined.cats._
import eu.timepit.refined.collection.{MaxSize, MinSize}
import spotification.domain.spotify.track.TrackUri
import spotification.domain.{SpotifyId, UriString}

package object playlist {
  @newtype case class PlaylistId(value: SpotifyId)
  object PlaylistId {
    implicit val PlaylistIdShow: Show[PlaylistId] = implicitly[Show[SpotifyId]].coerce
  }

  @newtype case class PlaylistApiUri(value: UriString)
  object PlaylistApiUri {
    implicit val PlaylistApiUriShow: Show[PlaylistApiUri] = implicitly[Show[UriString]].coerce
  }

  // A maximum of 100 Tracks can be added to a Playlist in a single request
  // An IndexedSeq is being used due to efficient `length` operation (needed for the refinement)
  type TrackUrisToAddMax = 100
  type TrackUrisToAddR = MinSize[1] And MaxSize[TrackUrisToAddMax]
  type TrackUrisToAdd = Vector[TrackUri] Refined TrackUrisToAddR
  object TrackUrisToAdd {
    val MaxSize: TrackUrisToAddMax = 100
  }
}
