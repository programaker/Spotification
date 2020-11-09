package spotification.track

import spotification.authorization.api.SpotifyAuthorizationLayer
import spotification.track.httpclient.TrackServiceLayer
import spotification.track.program.ShareTrackEnv
import zio.TaskLayer

package object api {
  val ShareTrackLayer: TaskLayer[ShareTrackEnv] =
    TrackServiceLayer ++ SpotifyAuthorizationLayer
}
