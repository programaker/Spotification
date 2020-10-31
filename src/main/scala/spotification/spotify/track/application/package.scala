package spotification.spotify.track

import cats.data.NonEmptyList
import spotification.common.application.refineRIO
import spotification.common.infra.httpclient.{H4sClient, HttpClientModule}
import spotification.config.TrackConfig
import spotification.config.application.TrackConfigModule
import spotification.spotify.authorization.AccessToken
import spotification.spotify.playlist.GetPlaylistsItemsResponse.TrackResponse
import spotification.spotify.playlist.{
  AddItemsToPlaylistRequest,
  GetPlaylistsItemsRequest,
  PlaylistId,
  PlaylistItemsToProcess,
  PlaylistItemsToProcessR,
  RemoveItemsFromPlaylistRequest
}
import spotification.spotify.playlist.application.{PlaylistModule, addItemsToPlaylist, removeItemsFromPlaylist}
import spotification.spotify.track.infra.H4sTrackService
import zio._

package object application {
  type TrackModule = Has[TrackService]
  object TrackModule {
    val live: TaskLayer[TrackModule] = {
      val l1 = ZLayer.fromServices[TrackConfig, H4sClient, TrackService] { (config, httpClient) =>
        new H4sTrackService(config.trackApiUri, httpClient)
      }

      (TrackConfigModule.live ++ HttpClientModule.live) >>> l1
    }
  }

  def getTrack(req: GetTrackRequest): RIO[TrackModule, GetTrackResponse] =
    ZIO.accessM(_.get.getTrack(req))
}
