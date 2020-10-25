package spotification.spotify.track

import spotification.config.TrackConfig
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

  def importTracks(
    trackUris: NonEmptyList[TrackUri],
    destPlaylist: PlaylistId,
    accessToken: AccessToken
  ): RIO[PlaylistModule, Unit] =
    ZIO.foreachPar_ {
      trackUris.toList
        .to(LazyList)
        .grouped(PlaylistItemsToProcess.maxSize)
        .map(_.toVector)
        .map(refineRIO[PlaylistModule, PlaylistItemsToProcessR](_))
        .map(_.flatMap(importTrackChunk(_, destPlaylist, accessToken)))
        .to(Iterable)
    }(identity)

  private def deleteTracks(
    items: NonEmptyList[TrackResponse],
    req: GetPlaylistsItemsRequest.FirstRequest
  ): RIO[PlaylistModule, Unit] =
    ZIO.foreachPar_(
      items.toList
        .to(LazyList)
        .map(_.uri)
        .grouped(PlaylistItemsToProcess.maxSize)
        .map(_.toVector)
        .map(refineRIO[PlaylistModule, PlaylistItemsToProcessR](_))
        .map(_.map(RemoveItemsFromPlaylistRequest.make(_, req.playlistId, req.accessToken)))
        .map(_.flatMap(removeItemsFromPlaylist))
        .to(Iterable)
    )(identity)

  private def importTrackChunk(
    trackUris: PlaylistItemsToProcess[TrackUri],
    destPlaylist: PlaylistId,
    accessToken: AccessToken
  ): RIO[PlaylistModule, Unit] =
    addItemsToPlaylist(AddItemsToPlaylistRequest.make(destPlaylist, trackUris, accessToken)).map(_ => ())
}
